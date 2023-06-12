package org.fhi360.plugins.impilo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Prescription;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.domain.repositories.PrescriptionRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.web.errors.AuthenticationError;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EHRService {
    private final static String BASE_URL = "http://197.221.242.150:10408";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;
    private final TransactionHandler transactionHandler;
    private final PatientRepository patientRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final RefillRepository refillRepository;
    private final ClinicDataRepository clinicDataRepository;

    @Transactional
    public boolean sync(String username, String password) throws URISyntaxException, IOException, InterruptedException {
        String token = getAuthorizationToken(username, password);
        processPatients(token);
        saveTransactions(token);
        return true;
    }

    public void processPatients(String token) throws URISyntaxException, IOException, InterruptedException {
        List<Map<String, Object>> patients = retrievePatients(token);
        List<Map<String, Object>> persons = retrievePersons(token);
        List<Map<String, Object>> phones = retrievePhones(token);
        List<Map<String, Object>> prescriptions = retrievePrescriptions(token);

        patients.forEach(p -> {
            Patient patient = patientRepository.findByPatientId(String.valueOf(p.get("id"))).orElse(new Patient());
            patient.setPatientId(String.valueOf(p.get("id")));
            patient.setPersonId(String.valueOf(p.get("personId")));
            patient.setHospitalNumber(String.valueOf(p.get("hospitalNumber")));
            patient.setFacilityId(String.valueOf(p.get("facilityId")));
            patient.setFacilityName(String.valueOf(p.get("facilityName")));

            persons.stream()
                .filter(ps -> Objects.equals(String.valueOf(ps.get("id")), String.valueOf(p.get("personId"))))
                .findFirst().ifPresent(person -> {
                    patient.setGivenName(String.valueOf(person.get("givenName")));
                    patient.setFamilyName(String.valueOf(person.get("familyName")));
                    patient.setSex(StringUtils.capitalize(StringUtils.lowerCase(String.valueOf(person.get("sex")))));
                    patient.setDateOfBirth((LocalDate) person.get("givenName"));
                    patient.setAddress(String.valueOf(person.get("address")));

                    phones.stream()
                        .filter(ph -> Objects.equals(String.valueOf(ph.get("personId")), String.valueOf(person.get("id"))))
                        .findFirst().ifPresent(phone -> patient.setPhoneNumber(String.valueOf(phone.get("number"))));

                    var result = patientRepository.save(patient);
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
                            prescriptionRepository.save(prescription);

                            result.setRegimen(pres.get("medicineName").toString());
                        });
                    patientRepository.save(patient);
                });

        });
    }

    public void saveTransactions(String token) throws URISyntaxException, IOException, InterruptedException {
        List<Map<String, Object>> dispenseDtoList = new ArrayList<>();
        List<Map<String, Object>> vitals = new ArrayList<>();

        refillRepository.findBySyncedIsFalse().forEach(refill -> {
            //batchIssueId
            Map<String, Object> dispenseDto = new HashMap<>();
            dispenseDto.put("dateCreated", refill.getDate());
            dispenseDto.put("batchIssueId", "");
            dispenseDto.put("facilityId", refill.getPatient().getFacilityId());
            dispenseDto.put("personId", refill.getPatient().getPersonId());
            dispenseDto.put("quantity", refill.getQtyDispensed() * 30);
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
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            refillRepository.updateSyncStatus();
            clinicDataRepository.updateSyncStatus();
        }
    }

    private List<Map<String, Object>> retrievePatients(String token) throws URISyntaxException, IOException, InterruptedException {
        List<Map<String, Object>> patients = new ArrayList<>();
        int page = 0;
        boolean lastPage = false;
        while (!lastPage) {
            var request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(BASE_URL + "/api/bulk/patient?size=50&sort=id&page=" + page))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonNode body = objectMapper.readTree(response.body());
            if (body.at("content").isArray()) {
                for (JsonNode record : body.at("content")) {
                    String patientId = record.at("/id").asText();
                    String hospitalNumber = record.at("/hospitalNumber").asText();
                    String facilityId = record.at("/facility/id").asText();
                    String facilityName = record.at("/facility/name").asText();
                    String personId = record.at("/personId").asText();
                    lastPage = body.at("/last").asBoolean();

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
        }

        return patients;
    }

    private List<Map<String, Object>> retrievePersons(String token) throws URISyntaxException, IOException, InterruptedException {
        List<Map<String, Object>> persons = new ArrayList<>();
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
            if (body.at("/content").isArray()) {
                for (JsonNode record : body.at("/content")) {
                    String id = record.at("/id").asText();
                    String givenName = record.at("/firstName").asText();
                    String familyName = record.at("/lastName").asText();
                    String sex = record.at("/sex").asText();
                    LocalDate dateOfBirth = LocalDate.parse(record.at("/birthDate").asText());
                    String street = record.at("/street").asText();
                    String city = record.at("/city").asText();
                    String town = record.at("/town/name").asText();
                    lastPage = body.at("/last").asBoolean();

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

    private List<Map<String, Object>> retrievePhones(String token) throws URISyntaxException, IOException, InterruptedException {
        List<Map<String, Object>> phones = new ArrayList<>();
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
            if (body.at("content").isArray()) {
                for (JsonNode record : body.at("content")) {
                    String number = record.at("/number").asText();
                    String personId = record.at("/personId").asText();
                    lastPage = body.at("/last").asBoolean();

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

    private List<Map<String, Object>> retrievePrescriptions(String token) throws URISyntaxException, IOException, InterruptedException {
        List<Map<String, Object>> prescriptions = new ArrayList<>();
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
            if (body.at("/content").isArray()) {
                for (JsonNode record : body.at("/content")) {
                    String id = record.at("/prescriptionId").asText();
                    String medicineId = record.at("/medicineId").asText();
                    String medicineName = record.at("/medicineName").asText();
                    String personId = record.at("/personId").asText();
                    String frequencyId = record.at("/frequencyId").asText();
                    LocalDateTime time = LocalDateTime.parse(record.at("/time").asText());
                    lastPage = body.at("/last").asBoolean();

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

    private String getAuthorizationToken(String username, String password) {
        try {
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
            if (response.statusCode() == 200) {
                return body.at("/id_token").asText();
            }
        } catch (Exception ignored) {

        }

        throw new AuthenticationError();
    }
}
