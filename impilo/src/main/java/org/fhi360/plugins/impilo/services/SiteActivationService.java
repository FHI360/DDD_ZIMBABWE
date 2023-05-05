package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import io.github.jbella.snl.core.api.domain.Organisation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Discontinuation;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.SiteAssignment;
import org.fhi360.plugins.impilo.domain.repositories.DiscontinuationRepository;
import org.fhi360.plugins.impilo.domain.repositories.SiteAssignmentRepository;
import org.fhi360.plugins.impilo.web.models.ActivationData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SiteActivationService {
    private final SiteAssignmentRepository siteAssignmentRepository;
    private final DiscontinuationRepository discontinuationRepository;
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    public ActivationData activate(String code) {
        var settings = EntityViewSetting.create(Organisation.ShortView.class);
        var cb = cbf.create(em, Organisation.class)
            .where("party.identifiers.value").eq(code);
        var organisations = evm.applySetting(settings, cb).getResultList();
        if (organisations.isEmpty()) {
            return new ActivationData();
        }
        var organisation = organisations.get(0);
        ActivationData data = new ActivationData();
        data.setSite(organisation.getName());
        var settings1 = EntityViewSetting.create(Patient.View.class);
        var cb1 = cbf.create(em, Patient.View.class);
        //@formatter:off
            cb1.from(Patient.class,"p")
                .joinOn(SiteAssignment.class, "s", JoinType.FULL)
                    .onExpression("s.patient = p")
                .end()
                .where("s.site.id").eq(organisation.getId());
            //@formatter:on
        var patients = evm.applySetting(settings1, cb1).getResultList();
        data.setPatients(patients);
        return data;
    }

    @Transactional
    public boolean discontinueClient(String siteCode, String uniqueId, LocalDate date, String reason) {
        var cb = cbf.create(em, SiteAssignment.class);
        //@formatter:off
        cb.from(SiteAssignment.class)
                .where("site.party.identifiers.value").eq(siteCode)
                .where("patient.uniqueId").eq(uniqueId);
        //@formatter:on
        try {
            var assignment = cb.getSingleResult();

            Discontinuation discontinuation = new Discontinuation();
            discontinuation.setDate(date);
            discontinuation.setReason(reason);
            discontinuation.setPatient(assignment.getPatient());

            discontinuationRepository.save(discontinuation);
            siteAssignmentRepository.delete(assignment);

            return true;
        } catch (NoResultException e) {
            return false;
        }
    }
}
