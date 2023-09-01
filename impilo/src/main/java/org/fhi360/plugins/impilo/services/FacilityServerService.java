package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.ConfigurationService;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.*;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.DevolveRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.domain.repositories.StockRequestRepository;
import org.fhi360.plugins.impilo.services.model.FacilityData;
import org.fhi360.plugins.impilo.services.model.ServerData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FacilityServerService {
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
    private final TransactionHandler transactionHandler;
    private String BASE_URL;

    public void synchronize() throws Exception {
        pushServerData();
        retrieveFacilityData();
    }

    private void retrieveFacilityData() throws Exception {
        var cb = cbf.create(em, Tuple.class)
            .from(Patient.class)
            .select("facilityId");
        var results = cb.getResultList();
        String facilityId = "";
        if (!results.isEmpty()) {
            facilityId = results.get(0).get(0, String.class);
        }

        var settings1 = EntityViewSetting.create(Organisation.CreateView.class);
        var cb1 = cbf.create(em, Organisation.class)
            .where("type").eq("OUTLET");
        var sites = evm.applySetting(settings1, cb1).getResultList();

        var token = authenticate();
        var request = HttpRequest.newBuilder()
            .GET()
            .uri(new URI(BASE_URL + "/api/impilo/server-sync/facility-data/" + facilityId))
            .header("Authorization", token)
            .header("Content-Type", "application/json")
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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

    private void pushServerData() throws Exception {
        ServerData data = new ServerData();

        var settings1 = EntityViewSetting.create(Organisation.CreateView.class);
        var cb1 = cbf.create(em, Organisation.class)
            .where("type").eq("OUTLET");
        var sites = evm.applySetting(settings1, cb1).getResultList();
        data.setOutlets(sites);

        settings1 = EntityViewSetting.create(Organisation.CreateView.class);
        cb1 = cbf.create(em, Organisation.class)
            .where("type").eq("FACILITY");
        var facilities = evm.applySetting(settings1, cb1).getResultList();
        data.setOutlets(facilities);

        var settings2 = EntityViewSetting.create(Patient.CreateView.class);
        var cb2 = cbf.create(em, Patient.class)
            .joinOn(Devolve.class, "s", JoinType.FULL)
            .onExpression("s.patient = p")
            .end();
        var patients = evm.applySetting(settings2, cb2).getResultList();
        data.setPatients(patients);

        var settings3 = EntityViewSetting.create(Devolve.CreateView.class);
        var cb3 = cbf.create(em, Devolve.class);
        var devolves = evm.applySetting(settings3, cb3).getResultList();
        data.setDevolves(devolves);

        var settings4 = EntityViewSetting.create(StockIssuance.CreateView.class);
        var cb4 = cbf.create(em, StockIssuance.class);
        var issuance = evm.applySetting(settings4, cb4).getResultList();
        data.setStockIssuance(issuance);

        var token = authenticate();
        var request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(data)))
            .uri(new URI(BASE_URL + "/api/impilo/sync/server-data"))
            .header("Authorization", token)
            .header("Content-Type", "application/json")
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String authenticate() throws Exception {
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
        return "Bearer " + StringUtils.trimToEmpty(body.at("/accessToken").asText());
    }

    @PostConstruct
    public void init() {
        BASE_URL = configurationService
            .getValueAsStringForKey("IMPILO.CONFIGURATION.SYNCHRONIZATION", "CENTRAL_SERVER.URL").orElse("");
    }
}
