package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The `PatientService` class is a Java service that provides methods to retrieve patient information and refill records
 * based on patient ID.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class PatientService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    /**
     * The function retrieves a patient's view by their ID and returns it as an Optional, or returns an empty Optional if
     * no patient is found.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that is used to identify a specific patient
     * in the system.
     * @return The method is returning an Optional object that contains an instance of the Patient.View class.
     */
    public Optional<Patient.View> getById(UUID id) {
        var settings = EntityViewSetting.create(Patient.View.class);
        var cb = cbf.create(em, Patient.class)
            .where("id").eq(id);
        try {
            return Optional.of(evm.applySetting(settings, cb).getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * The function retrieves a list of Refill objects filtered by a specific patient ID and ordered by date and ID.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that represents the unique identifier of a
     * patient.
     * @return The method is returning a List of objects of type Refill.ListView.
     */
    public List<Refill.ListView> getByPatient(UUID id) {
        var settings = EntityViewSetting.create(Refill.ListView.class);
        var cb = cbf.create(em, Refill.class)
            .where("patient.id").eq(id)
            .orderByDesc("date")
            .orderByAsc("id");

        return evm.applySetting(settings, cb).getResultList();
    }
}
