package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.domain.Preference;
import io.github.jbella.snl.core.api.services.ConfigurationService;
import io.github.jbella.snl.core.api.services.ExtensionService;
import io.github.jbella.snl.core.api.services.PreferenceService;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.*;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.DevolveRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.domain.repositories.StockRequestRepository;
import org.fhi360.plugins.impilo.services.models.FacilityData;
import org.fhi360.plugins.impilo.services.models.ServerData;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The FacilityServerService class is responsible for synchronizing data between the local server and a central server, as
 * well as scheduling and executing the synchronization task at regular intervals.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FacilityServerService {
    private final static String CATEGORY = "IMPILO.FACILITY.AUTO_SYNC";
    private final static String KEY = "SYNC_INTERVAL";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final EntityViewManager evm;
    private final RefillRepository refillRepository;
    private final ClinicDataRepository clinicDataRepository;
    private final DevolveRepository devolveRepository;
    private final StockRequestRepository stockRequestRepository;
    private final ConfigurationService configurationService;
    private final ExtensionService extensionService;
    private final TaskScheduler taskScheduler;
    private final TransactionHandler transactionHandler;
    private final PreferenceService preferenceService;
    private ScheduledFuture<?> scheduledFuture;
    private String BASE_URL;

    /**
     * The function "synchronize" pushes server data and retrieves facility data.
     */
    public void synchronize() throws Exception {
        pushServerData();
        retrieveFacilityData();
    }

    /**
     * The function schedules a task to be executed at a fixed rate, with the specified interval in seconds.
     *
     * @param interval The interval parameter represents the time interval in seconds at which the task should be scheduled
     *                 to run.
     */
    public void scheduleTask(long interval) {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                synchronize();
            } catch (Exception ignored) {

            }
        }, Duration.of(interval, ChronoUnit.SECONDS));
        var _preference = new Preference();
        _preference.setCategory(CATEGORY);
        _preference.setKey(KEY);
        var preference = preferenceService.getPreference(CATEGORY, KEY).orElse(_preference);
        preference.setValue(Long.toString(interval));
        preferenceService.save(preference);
    }

    public Long getCurrentAutoSyncInterval() {
        try {
            return Long.parseLong(preferenceService.getPreference(CATEGORY, KEY)
                    .map(Preference::getValue).orElse("0"));
        } catch (Exception e) {
            return 0L;
        }
    }

    @PostConstruct
    public void init() {
        final long TEN_MINUTES = TimeUnit.MINUTES.toSeconds(10);
        scheduleTask(TEN_MINUTES);
    }

    /**
     * The `retrieveFacilityData()` function retrieves facility data from a server, processes it, and saves it to the
     * database.
     */
    private void retrieveFacilityData() throws Exception {
        BASE_URL = getBaseUrl();
        UUID facilityId = extensionService.getPriorityExtension(AuthenticationServiceExtension.class).organisation().getId();

        var settings1 = EntityViewSetting.create(Organisation.IdView.class);
        var cb1 = cbf.create(em, Organisation.class)
                .where("type").eq("OUTLET");
        var outlets = evm.applySetting(settings1, cb1).getResultList();
        var ids = outlets.stream()
                .map(v -> "outletIds=" + v.getId())
                .collect(Collectors.joining("&"));

        var token = authenticate();
        var request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(BASE_URL + "/api/impilo/server-sync/facility-data/" + facilityId + "?" + ids))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }
        FacilityData facilityData = objectMapper.readValue(response.body(), FacilityData.class);

        boolean success = transactionHandler.runInTransaction(() -> {
            try {
                var clinics = facilityData.getClinics().stream()
                        .map(c -> {
                            var _clinic = objectMapper.convertValue(c, ClinicData.class);
                            var clinic = clinicDataRepository.findByReference(c.getReference()).orElse(_clinic);
                            BeanUtils.copyProperties(_clinic, clinic, "id");
                            return clinic;
                        })
                        .toList();
                clinicDataRepository.saveAll(clinics);

                var refills = facilityData.getRefills().stream()
                        .map(r -> {
                            var _refill = objectMapper.convertValue(r, Refill.class);
                            var refill = refillRepository.findByReference(r.getReference()).orElse(_refill);
                            BeanUtils.copyProperties(_refill, refill, "id");
                            return refill;
                        })
                        .toList();
                refillRepository.saveAll(refills);

                var devolves = facilityData.getDevolves().stream()
                        .map(d -> {
                            var _ds = objectMapper.convertValue(d, Devolve.class);
                            var ds = devolveRepository.findByReference(d.getReference()).orElse(_ds);
                            BeanUtils.copyProperties(_ds, ds, "id");
                            return ds;
                        })
                        .toList();
                devolveRepository.saveAll(devolves);

                var requests = facilityData.getStockRequest().stream()
                        .map(r -> {
                            var _req = objectMapper.convertValue(r, StockRequest.class);
                            var req = stockRequestRepository.findByReference(r.getReference()).orElse(_req);
                            BeanUtils.copyProperties(_req, req, "id");
                            return req;
                        })
                        .toList();
                stockRequestRepository.saveAll(requests);
            } catch (Exception e) {
                return false;
            }
            return true;
        });
        if (success) {

            try {
                request = HttpRequest.newBuilder()
                        .GET()
                        .uri(new URI(BASE_URL + "/api/impilo/server-sync/acknowledge/" + facilityData.getReference()))
                        .header("Authorization", token)
                        .header("Content-Type", "application/json")
                        .build();
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * The `pushServerData()` function sends a POST request to a server with various data objects in the body, after
     * authenticating the request.
     */
    private void pushServerData() throws Exception {
        BASE_URL = getBaseUrl();
        ServerData data = new ServerData();

        var settings1 = EntityViewSetting.create(Organisation.UpdateView.class);
        var cb1 = cbf.create(em, Organisation.class)
                .where("type").eq("OUTLET");
        var outlets = evm.applySetting(settings1, cb1).getResultList();
        data.setOutlets(outlets);

        settings1 = EntityViewSetting.create(Organisation.UpdateView.class);
        cb1 = cbf.create(em, Organisation.class)
                .where("type").eq("FACILITY");
        var facilities = evm.applySetting(settings1, cb1).getResultList();
        data.setFacilities(facilities);

        var settings2 = EntityViewSetting.create(Patient.UpdateView.class);
        //@formatter:off
        var cb2 = cbf.create(em, Patient.class, "p")
            .joinOn(Devolve.class, "d", JoinType.INNER)
                .onExpression("d.patient.id = p.id")
            .end();
        var patients = evm.applySetting(settings2, cb2).getResultList();
        //@formatter:on
        data.setPatients(patients);

        var settings3 = EntityViewSetting.create(Devolve.CreateView.class);
        var cb3 = cbf.create(em, Devolve.class)
                .where("synced").eq(false);
        var devolves = evm.applySetting(settings3, cb3).getResultList();
        data.setDevolves(devolves);

        var settings4 = EntityViewSetting.create(StockIssuance.CreateView.class);
        var cb4 = cbf.create(em, StockIssuance.class)
                .where("synced").eq(false);
        var issuance = evm.applySetting(settings4, cb4).getResultList();
        data.setStockIssuance(issuance);

        var settings5 = EntityViewSetting.create(Stock.CreateView.class);
        var cb5 = cbf.create(em, Stock.class);
        var stocks = evm.applySetting(settings5, cb5).getResultList();
        data.setStocks(stocks);

        var settings6 = EntityViewSetting.create(StockRequest.UpdateView.class);
        var cb6 = cbf.create(em, StockRequest.class);
        var requests = evm.applySetting(settings6, cb6).getResultList();
        data.setStockRequests(requests);

        var token = authenticate();
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data)))
                .uri(new URI(BASE_URL + "/api/impilo/server-sync/server-data"))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }
    }

    private String authenticate() throws Exception {
        BASE_URL = getBaseUrl();
        String username = configurationService.getValueAsStringForKey("IMPILO.CONFIGURATION.SYNCHRONIZATION", "CENTRAL_SERVER.USERNAME")
                .orElse("");
        String password = configurationService.getValueAsStringForKey("IMPILO.CONFIGURATION.SYNCHRONIZATION", "CENTRAL_SERVER.PASSWORD")
                .orElse("");
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);
        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                .uri(new URI(BASE_URL + "/api/authenticate"))
                .header("Authorization", "")
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode body = objectMapper.readTree(response.body());
        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }
        return "Bearer " + StringUtils.trimToEmpty(body.at("/accessToken").asText());
    }

    private String getBaseUrl() {
        return configurationService
                .getValueAsStringForKey("IMPILO.CONFIGURATION.SYNCHRONIZATION", "CENTRAL_SERVER.URL").orElse("");
    }

}
