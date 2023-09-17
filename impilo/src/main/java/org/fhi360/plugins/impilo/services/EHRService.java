package org.fhi360.plugins.impilo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.ConfigurationService;
import io.github.jbella.snl.core.api.services.ExtensionService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Prescription;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.domain.repositories.PrescriptionRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * The `EHRService` class is responsible for retrieving patient data from an EHR server, processing and saving it to
 * various repositories, and synchronizing data with an external system.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EHRService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final RefillRepository refillRepository;
    private final ClinicDataRepository clinicDataRepository;
    private final ConfigurationService configurationService;
    private final ExtensionService extensionService;
    private String BASE_URL;

    /**
     * The `processPatients` function retrieves patient, person, phone, and prescription data, and then updates the patient
     * records with the corresponding information.
     */
    public void processPatients() throws Exception {
        try {
            List<Map<String, Object>> patients = retrievePatients();
            List<Map<String, Object>> persons = retrievePersons();
            List<Map<String, Object>> phones = retrievePhones();
            List<Map<String, Object>> prescriptions = retrievePrescriptions();
            var org = extensionService.getPriorityExtension(AuthenticationServiceExtension.class).organisation();
            Organisation organisation = objectMapper.convertValue(org, Organisation.class);

            patients.forEach(p -> {
                Patient patient = patientRepository.findByPatientId(String.valueOf(p.get("id"))).orElse(new Patient());
                patient.setPatientId(String.valueOf(p.get("patientId")));
                patient.setReference(UUID.fromString(String.valueOf(p.get("patientId"))));
                patient.setPersonId(String.valueOf(p.get("personId")));
                patient.setHospitalNumber(String.valueOf(p.get("hospitalNumber")));
                patient.setFacilityId(String.valueOf(p.get("facilityId")));
                patient.setFacilityName(String.valueOf(p.get("facilityName")));

                persons.stream()
                    .filter(ps -> Objects.equals(String.valueOf(ps.get("id")), String.valueOf(p.get("personId"))))
                    .findFirst().ifPresent(person -> {
                        patient.setGivenName(String.valueOf(person.get("givenName")));
                        patient.setFamilyName(String.valueOf(person.get("familyName")));
                        patient.setSex(StringUtils.toRootLowerCase(String.valueOf(person.get("sex"))));
                        patient.setDateOfBirth((LocalDate) person.get("dateOfBirth"));
                        patient.setAddress(String.valueOf(person.get("address")));

                        phones.stream()
                            .filter(ph -> Objects.equals(String.valueOf(ph.get("personId")), String.valueOf(person.get("id"))))
                            .findFirst().ifPresent(phone -> patient.setPhoneNumber(String.valueOf(phone.get("number"))));

                        patient.setOrganisation(organisation);
                        var _patient = patientRepository.findByReference(patient.getReference()).orElse(patient);
                        BeanUtils.copyProperties(patient, _patient);
                        var result = patientRepository.save(_patient);

                        prescriptions.stream()
                            .filter(pres -> Objects.equals(String.valueOf(pres.get("personId")), String.valueOf(person.get("id"))))
                            .sorted(Comparator.comparing(p2 -> ((LocalDateTime) p2.get("time"))))
                            .forEach(pres -> {
                                Prescription prescription = new Prescription();
                                prescription.setPatient(result);
                                prescription.setPrescriptionId(String.valueOf(pres.get("prescriptionId")));
                                prescription.setMedicineId(String.valueOf(pres.get("medicineId")));
                                prescription.setFrequencyId(String.valueOf(pres.get("frequencyId")));
                                prescription.setTime((LocalDateTime) pres.get("time"));

                                var _prescription = prescriptionRepository.findByPrescriptionId(prescription.getPrescriptionId())
                                    .orElse(prescription);
                                BeanUtils.copyProperties(prescription, _prescription);
                                prescriptionRepository.save(_prescription);

                                result.setRegimen(pres.get("medicineName").toString());
                            });

                        patientRepository.save(result);
                    });

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The `saveTransactions` function saves unsynced refill and clinic data transactions to EHR server using an HTTP
     * POST request.
     */
    @Transactional
    public void saveTransactions() throws Exception {
        String token = authenticate();
        List<Map<String, Object>> dispenseDtoList = new ArrayList<>();
        List<Map<String, Object>> vitals = new ArrayList<>();

        refillRepository.findBySyncedIsFalse().forEach(refill -> {
            //batchIssueId
            Map<String, Object> dispenseDto = new HashMap<>();
            dispenseDto.put("dateCreated", refill.getDate());
            dispenseDto.put("facilityId", refill.getPatient().getFacilityId());
            dispenseDto.put("personId", refill.getPatient().getPersonId());
            dispenseDto.put("quantity", refill.getQtyDispensed() * 30);
            dispenseDto.put("batchIssueId", refill.getBatchIssuanceId());
            prescriptionRepository.findLastByPatient(refill.getPatient()).ifPresent(prescription -> {
                dispenseDto.put("frequencyId", prescription.getFrequencyId());
                dispenseDto.put("medicineId", prescription.getMedicineId());
                dispenseDto.put("prescriptionId", prescription.getPrescriptionId());
            });
            dispenseDtoList.add(dispenseDto);
        });

        clinicDataRepository.findBySyncedIsFalse().forEach(clinicData -> {
            //visitId
            //value
            //vitalName
            Map<String, Object> vitalDto = new HashMap<>();
            vitalDto.put("dateTime", LocalDateTime.of(clinicData.getDate(), LocalTime.MIDNIGHT));
            vitalDto.put("diastolic", clinicData.getDiastolic());
            vitalDto.put("systolic", clinicData.getSystolic());
            vitalDto.put("personId", clinicData.getPatient().getPersonId());
            vitalDto.put("vitalName", "BLOOD_PRESSURE");
            vitals.add(vitalDto);
        });

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("dispenseDtoList", dispenseDtoList);
        requestBody.put("vitals", vitals);

        var request = HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
            .uri(new URI(BASE_URL + "/api/data-sync/patient"))
            .header("Authorization", token)
            .header("Content-Type", "application/json")
            .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode body = objectMapper.readTree(response.body());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            refillRepository.updateSyncStatus();
            clinicDataRepository.updateSyncStatus();
        }
    }

    private List<Map<String, Object>> retrievePatients() throws Exception {
        BASE_URL = getBaseUrl();
        List<Map<String, Object>> patients = new ArrayList<>();
        var token = authenticate();
        int page = 0;
        boolean lastPage = false;
        while (!lastPage) {
            var request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(BASE_URL + "/api/bulk/patient?size=50&sort=id&page=" + page))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode body = objectMapper.readTree(response.body());
                lastPage = body.at("/last").asBoolean();
                if (body.at("/content").isArray()) {
                    for (JsonNode record : body.at("/content")) {
                        String patientId = record.at("/id").asText();
                        String hospitalNumber = record.at("/hospitalNumber").asText();
                        String facilityId = record.at("/facility/id").asText();
                        String facilityName = record.at("/facility/name").asText();
                        String personId = record.at("/personId").asText();

                        Map<String, Object> patient = new HashMap<>();
                        patient.put("patientId", patientId);
                        patient.put("facilityId", facilityId);
                        patient.put("facilityName", facilityName);
                        patient.put("hospitalNumber", hospitalNumber);
                        patient.put("personId", personId);

                        patients.add(patient);
                        ++page;
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return patients;
    }

    private List<Map<String, Object>> retrievePersons() throws Exception {
        BASE_URL = getBaseUrl();
        List<Map<String, Object>> persons = new ArrayList<>();
        var token = authenticate();
        int page = 0;
        boolean lastPage = false;
        while (!lastPage) {
            var request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(BASE_URL + "/api/bulk/person?size=50&sort=id&page=" + page))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());
            lastPage = body.at("/last").asBoolean();
            if (body.at("/content").isArray()) {
                for (JsonNode record : body.at("/content")) {
                    String id = record.at("/id").asText();
                    String givenName = record.at("/firstName").asText();
                    String familyName = record.at("/lastName").asText();
                    String sex = record.at("/sex").asText();
                    LocalDate dateOfBirth = LocalDate.parse(record.at("/birthDate").asText());
                    String street = record.at("/address/street").asText();
                    String city = record.at("/address/city").asText();
                    String town = record.at("/address/town/name").asText();

                    Map<String, Object> person = new HashMap<>();
                    person.put("id", id);
                    person.put("givenName", givenName);
                    person.put("familyName", familyName);
                    person.put("sex", sex);
                    person.put("dateOfBirth", dateOfBirth);
                    person.put("address", String.format("%s%s%s", street, StringUtils.isNotBlank(city) ? ", " + city : "",
                        StringUtils.isNotBlank(town) ? ", " + town : ""));

                    persons.add(person);
                    ++page;
                }
            }
        }

        return persons;
    }

    private List<Map<String, Object>> retrievePhones() throws Exception {
        BASE_URL = getBaseUrl();
        List<Map<String, Object>> phones = new ArrayList<>();
        var token = authenticate();
        int page = 0;
        boolean lastPage = false;
        while (!lastPage) {
            var request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(BASE_URL + "/api/bulk/person/phone?size=50&sort=id&page=" + page))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());
            lastPage = body.at("/last").asBoolean();
            if (body.at("/content").isArray()) {
                for (JsonNode record : body.at("/content")) {
                    String number = record.at("/number").asText();
                    String personId = record.at("/personId").asText();

                    Map<String, Object> phone = new HashMap<>();
                    phone.put("number", number);
                    phone.put("personId", personId);

                    phones.add(phone);
                    ++page;
                }
            }
        }

        return phones;
    }

    private List<Map<String, Object>> retrievePrescriptions() throws Exception {
        BASE_URL = getBaseUrl();
        List<Map<String, Object>> prescriptions = new ArrayList<>();
        var token = authenticate();
        int page = 0;
        boolean lastPage = false;
        while (!lastPage) {
            var request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(BASE_URL + "/api/bulk/person/prescription?size=50&sort=id&page=" + page))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());
            lastPage = body.at("/last").asBoolean();
            if (body.at("/content").isArray()) {
                for (JsonNode record : body.at("/content")) {
                    String id = record.at("/prescriptionId").asText();
                    String medicineId = record.at("/medicineId").asText();
                    String medicineName = record.at("/medicineName").asText();
                    String personId = record.at("/personId").asText();
                    String frequencyId = record.at("/frequencyId").asText();
                    LocalDateTime time = LocalDateTime.parse(record.at("/time").asText());

                    Map<String, Object> prescription = new HashMap<>();
                    prescription.put("id", id);
                    prescription.put("medicineId", medicineId);
                    prescription.put("medicineName", medicineName);
                    prescription.put("personId", personId);
                    prescription.put("frequencyId", frequencyId);
                    prescription.put("time", time);

                    prescriptions.add(prescription);
                    ++page;
                }
            }
        }

        return prescriptions;
    }

    private String authenticate() throws Exception {
        BASE_URL = getBaseUrl();
        String username = configurationService.getValueAsStringForKey("IMPILO.CONFIGURATION.SYNCHRONIZATION", "EHR_SERVER.USERNAME")
            .orElse("");
        String password = configurationService.getValueAsStringForKey("IMPILO.CONFIGURATION.SYNCHRONIZATION", "EHR_SERVER.PASSWORD")
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
        if (response.statusCode() != 200) {
            throw new RuntimeException(response.body());
        }
        JsonNode body = objectMapper.readTree(response.body());
        return "Bearer " + StringUtils.trimToEmpty(body.at("/id_token").asText());
    }
    private String getBaseUrl() {
        return configurationService.getValueAsStringForKey("IMPILO.CONFIGURATION.SYNCHRONIZATION", "EHR_SERVER.URL")
            .orElse("");
    }
}
