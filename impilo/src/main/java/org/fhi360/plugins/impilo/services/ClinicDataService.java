package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The `ClinicDataService` class is a service that saves clinic data for a specific patient identified by a unique ID.
 */
@Service
@RequiredArgsConstructor
public class ClinicDataService {
    private final ClinicDataRepository clinicDataRepository;
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final ObjectMapper objectMapper;

    /**
     * The `save` function saves clinic data associated with a patient identified by a unique ID.
     *
     * @param uniqueId The `uniqueId` parameter is a string that represents the unique identifier of a patient.
     * @param data The `data` parameter is an object of type `T`, which extends the `ClinicData.CreateView` interface.
     * @return The method returns a boolean value. It returns true if the data is successfully saved, and false if no
     * patient with the given uniqueId is found.
     */
    @Transactional
    public <T extends ClinicData.CreateView> boolean save(String uniqueId, T data) {
        var settings = EntityViewSetting.create(Patient.IdView.class);
        var cb = cbf.create(em, Patient.IdView.class);
        //@formatter:off
        cb.from(Patient.class)
                .where("uniqueId").eq(uniqueId);
        //@formatter:on
        try {
            var patient = evm.applySetting(settings, cb).getSingleResult();

            data.setPatient(patient);
            clinicDataRepository.save(objectMapper.convertValue(data, ClinicData.class));

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}
