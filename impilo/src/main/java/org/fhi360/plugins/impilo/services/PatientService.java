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

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class PatientService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

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

    public List<Refill.ListView> getByPatient(UUID id) {
        var settings = EntityViewSetting.create(Refill.ListView.class);
        var cb = cbf.create(em, Refill.class)
            .where("patient.id").eq(id)
            .orderByDesc("date")
            .orderByAsc("id");

        return evm.applySetting(settings, cb).getResultList();
    }
}
