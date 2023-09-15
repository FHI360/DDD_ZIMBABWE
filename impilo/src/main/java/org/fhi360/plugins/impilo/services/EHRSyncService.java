package org.fhi360.plugins.impilo.services;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Prescription;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.domain.repositories.PrescriptionRepository;
import org.fhi360.plugins.impilo.domain.repositories.StockRepository;
import org.fhi360.plugins.impilo.services.models.EHRSyncData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class EHRSyncService {
    private final PatientRepository patientRepository;
    private final StockRepository stockRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public void syncData(EHRSyncData data) {
        data.getPatients().forEach(_data -> {
            var patient = patientRepository.findByReference(_data.getPatientId()).orElse(new Patient());
            BeanUtils.copyProperties(_data, patient);
            String regimen =  _data.getPrescriptions().stream()
                    .min((p1, p2) -> p2.getTime().compareTo(p1.getTime()))
                    .map(org.fhi360.plugins.impilo.services.models.Prescription::getMedicineName)
                    .orElse("");
            patient.setRegimen(regimen);
            var result = patientRepository.save(patient);
            _data.getPrescriptions().stream()
                    .sorted(Comparator.comparing(org.fhi360.plugins.impilo.services.models.Prescription::getTime))
                    .forEach(_prescription -> {
                        var prescription = prescriptionRepository.findByPrescriptionId(_prescription.getPrescriptionId())
                                .orElse(new Prescription());
                        prescription.setPatient(result);
                        BeanUtils.copyProperties(_prescription, prescription);
                        prescriptionRepository.save(prescription);
                    });
        });
    }
}
