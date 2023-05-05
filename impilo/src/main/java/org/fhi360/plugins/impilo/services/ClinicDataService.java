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

@Service
@RequiredArgsConstructor
public class ClinicDataService {
    private final ClinicDataRepository clinicDataRepository;
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final ObjectMapper objectMapper;

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
