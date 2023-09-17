package org.fhi360.plugins.impilo.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Prescription;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.fhi360.plugins.impilo.domain.repositories.*;
import org.fhi360.plugins.impilo.services.models.Dispense;
import org.fhi360.plugins.impilo.services.models.EHRData;
import org.fhi360.plugins.impilo.services.models.EHRSyncData;
import org.fhi360.plugins.impilo.services.models.Vitals;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EHRSyncService {
    private final PatientRepository patientRepository;
    private final StockRepository stockRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final RefillRepository refillRepository;
    private final ClinicDataRepository clinicDataRepository;
    private final ObjectMapper objectMapper;
    private final ExtensionService extensionService;
    private final Map<UUID, Map<String, List<UUID>>> acknowledgements = new HashMap<>();

    @Transactional
    public void syncData(EHRSyncData data) {
        var org = extensionService.getPriorityExtension(AuthenticationServiceExtension.class).organisation();
        Organisation organisation = objectMapper.convertValue(org, Organisation.class);
        data.getPatients().forEach(_data -> {
            var patient = patientRepository.findByReference(_data.getPatientId()).orElse(new Patient());
            BeanUtils.copyProperties(_data, patient);
            String regimen = _data.getPrescriptions().stream()
                    .min((p1, p2) -> p2.getTime().compareTo(p1.getTime()))
                    .map(prescription -> prescription.getMedicineName())
                    .orElse("");
            patient.setRegimen(regimen);
            patient.setOrganisation(organisation);
            patient.setReference(_data.getPatientId());
            var result = patientRepository.save(patient);
            _data.getPrescriptions().stream()
                    .sorted(Comparator.comparing(prescription -> prescription.getTime()))
                    .forEach(_prescription -> {
                        var prescription = prescriptionRepository.findByPrescriptionId(_prescription.getPrescriptionId())
                                .orElse(new Prescription());
                        prescription.setPatient(result);
                        BeanUtils.copyProperties(_prescription, prescription);
                        prescriptionRepository.save(prescription);
                    });
        });

        data.getStocks().forEach(_stock -> {
            Stock stock = stockRepository.findByBatchIssuanceId(_stock.getBatchIssueId()).orElse(new Stock());
            stock.setFacility(organisation);
            stockRepository.save(stock);
        });
    }

    public EHRData getData() {
        EHRData data = new EHRData();
        List<Dispense> dispenses = new ArrayList<>();
        List<Vitals> vitals = new ArrayList<>();
        List<UUID> dispenseAck = new ArrayList<>();
        List<UUID> vitalsAck = new ArrayList<>();

        refillRepository.findBySyncedIsFalse().forEach(refill -> {
            Dispense dispense = new Dispense();
            dispense.setDateCreated(refill.getDate());
            dispense.setFacilityId(refill.getPatient().getFacilityId());
            dispense.setPersonId(refill.getPatient().getPersonId());
            dispense.setQuantity(refill.getQtyDispensed() * 30);
            dispense.setBatchIssueId(refill.getBatchIssuanceId());
            prescriptionRepository.findLastByPatient(refill.getPatient()).ifPresent(prescription -> {
                dispense.setFrequencyId(prescription.getFrequencyId());
                dispense.setMedicineId(prescription.getMedicineId());
                dispense.setPrescriptionId(prescription.getPrescriptionId());
            });

            dispenses.add(dispense);
            dispenseAck.add(refill.getId());
        });

        clinicDataRepository.findBySyncedIsFalse().forEach(clinicData -> {
            Vitals vital = new Vitals();
            vital.setDateTime(LocalDateTime.of(clinicData.getDate(), LocalTime.MIDNIGHT));
            vital.setDiastolic(clinicData.getDiastolic());
            vital.setSystolic(clinicData.getSystolic());
            vital.setPersonId(clinicData.getPatient().getPersonId());

            vitals.add(vital);
            vitalsAck.add(clinicData.getId());
        });

        data.setDispenses(dispenses);
        data.setVitals(vitals);

        Map<String, List<UUID>> acknowledgement = new HashMap<>();
        acknowledgement.put("dispenses", dispenseAck);
        acknowledgement.put("vitals", vitalsAck);
        acknowledgements.put(data.getReference(), acknowledgement);

        return data;
    }

    @Transactional
    public void acknowledge(UUID reference) {
        Map<String, List<UUID>> acknowledgement = acknowledgements.get(reference);
        if (acknowledgement != null) {
            acknowledgement.get("dispenses").forEach(refillRepository::updateSyncStatus);
            acknowledgement.get("vitals").forEach(clinicDataRepository::updateSyncStatus);

            acknowledgements.remove(reference);
        }
    }
}
