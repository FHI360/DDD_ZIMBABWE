package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.ExtensionService;
import io.github.jbella.snl.core.api.services.errors.BadRequestException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Prescription;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.fhi360.plugins.impilo.domain.repositories.*;
import org.fhi360.plugins.impilo.services.models.*;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('EHR Sync')")
public class EHRSyncService {
    private final PatientRepository patientRepository;
    private final StockRepository stockRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final RefillRepository refillRepository;
    private final ClinicDataRepository clinicDataRepository;
    private final ObjectMapper objectMapper;
    private final ExtensionService extensionService;
    private final EntityManager em;
    private final EntityViewManager evm;
    private final CriteriaBuilderFactory cbf;
    private final Map<UUID, Map<String, List<UUID>>> acknowledgements = new HashMap<>();

    private static Clinic getClinic(ClinicData clinicData) {
        Clinic c = new Clinic();
        c.setDate(clinicData.getDate());
        c.setPersonId(clinicData.getPatient().getPersonId());
        c.setCoughing(clinicData.getCoughing());
        c.setFever(clinicData.getFever());
        c.setSwelling(clinicData.getSwelling());
        c.setWeightLoss(clinicData.getWeightLoss());
        c.setWeight(clinicData.getWeight());
        c.setTemperature(clinicData.getTemperature());
        c.setSweating(clinicData.getSweating());
        c.setTbReferred(clinicData.getTbReferred());
        return c;
    }

    @Transactional
    public boolean syncData(EHRSyncData data) {
        var org = extensionService.getPriorityExtension(AuthenticationServiceExtension.class).organisation();
        Organisation organisation = objectMapper.convertValue(org, Organisation.class);

        var settings = EntityViewSetting.create(Organisation.CreateView.class);
        var cb = cbf.create(em, Organisation.class)
                .where("id").eq(org.getId());
        var res = evm.applySetting(settings, cb).getSingleResult();
        if (!Objects.equals(res.getType(), "FACILITY")) {
            throw new BadRequestException("Sync user must be assigned to organisation of type 'Facility'");
        }
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
            _data.getPrescriptions()
                .forEach(_prescription -> {
                    var prescription = prescriptionRepository
                        .findByPrescriptionIdAndPatient(_prescription.getPrescriptionId(), result).orElse(new Prescription());
                    prescription.setPatient(result);
                    BeanUtils.copyProperties(_prescription, prescription);
                    prescription = prescriptionRepository.save(prescription);
                });
        });

        data.getStocks().forEach(_stock -> {
            Stock stock = stockRepository.findByBatchIssuanceId(_stock.getBatchIssuanceId())
                .orElse(new Stock());
            BeanUtils.copyProperties(_stock, stock, "id");
            if (stock.getId() == null) {
                stock.setReference(UUID.randomUUID());
            }

            stock.setFacility(organisation);

            stockRepository.save(stock);
        });
        return true;
    }

    public EHRData getData(boolean all) {
        EHRData data = new EHRData();
        List<Dispense> dispenses = new ArrayList<>();
        List<Vitals> vitals = new ArrayList<>();
        List<Clinic> clinic = new ArrayList<>();
        List<UUID> dispenseAck = new ArrayList<>();
        List<UUID> vitalsAck = new ArrayList<>();

        refillRepository.findForSync(all).forEach(refill -> {
            Dispense dispense = new Dispense();
            dispense.setDateCreated(refill.getDate());
            dispense.setFacilityId(refill.getPatient().getFacilityId());
            dispense.setPersonId(refill.getPatient().getPersonId());
            dispense.setQuantity(refill.getQtyDispensed() * 30);
            dispense.setBatchIssueId(refill.getBatchIssuanceId());
            prescriptionRepository.findFirstByPatientOrderByTimeDesc(refill.getPatient()).ifPresent(prescription -> {
                dispense.setFrequencyId(prescription.getFrequencyId());
                dispense.setMedicineId(prescription.getMedicineId());
                dispense.setPrescriptionId(prescription.getPrescriptionId());
            });

            dispenses.add(dispense);
            dispenseAck.add(refill.getId());
        });

        clinicDataRepository.findForSync(all).forEach(clinicData -> {
            Vitals vital = new Vitals();
            vital.setDateTime(LocalDateTime.of(clinicData.getDate(), LocalTime.MIDNIGHT));
            vital.setDiastolic(clinicData.getDiastolic());
            vital.setSystolic(clinicData.getSystolic());
            vital.setPersonId(clinicData.getPatient().getPersonId());

            if (vital.hasData()) {
                vitals.add(vital);
                vitalsAck.add(clinicData.getId());
            }

            Clinic c = getClinic(clinicData);
            if (c.hasData()) {
                clinic.add(c);
            }
        });

        data.setDispenses(dispenses);
        data.setVitals(vitals);
        data.setClinicData(clinic);

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
