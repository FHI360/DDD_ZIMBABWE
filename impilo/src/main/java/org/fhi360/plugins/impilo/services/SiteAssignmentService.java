package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.errors.BadRequestException;
import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.SiteAssignment;
import org.fhi360.plugins.impilo.domain.repositories.SiteAssignmentRepository;
import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SiteAssignmentService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final PluginManager pluginManager;
    private final SiteAssignmentRepository siteAssignmentRepository;

    public PagedResult<Patient.ListView> list(String keyword, Boolean assigned, int start, int size) {
        var settings = EntityViewSetting.create(Patient.ListView.class, start * size, size);

        var cb = cbf.create(em, Patient.class);
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + StringUtils.trim(keyword) + "%";
            //@formatter:off
            cb.whereOr()
                .where("givenName").like(false).value(keyword).noEscape()
                .where("familyName").like(false).value(keyword).noEscape()
                .where("hospitalNumber").like(false).value(keyword).noEscape()
                .where("uniqueId").like(false).value(keyword).noEscape()
            .endOr();
            //@formatter:on
        }
        if (Objects.nonNull(assigned)) {
            if (assigned) {
                //@formatter:off
                cb.joinOn(SiteAssignment.class, "a", JoinType.INNER)
                    .onExpression("id = a.patient.id")
                .end();
                //@formatter:on
            }
            if (!assigned) {
                //@formatter:off
                cb.joinOn(SiteAssignment.class, "b", JoinType.LEFT)
                    .onExpression("id = b.patient.id")
                .end()
                .where("b.id").isNull();
                //@formatter:on
            }
        }
        var hierarchy = pluginManager.getExtensions(AuthenticationServiceExtension.class).stream().map(AuthenticationServiceExtension::getOrganisationHierarchy).findFirst().orElse(new ArrayList<>());
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("Facility")) {
                cb.where("facility.id").eq(hierarchy.get(0).getId());
            }
        }

        cb.orderByAsc("givenName")
            .orderByDesc("familyName")
            .orderByAsc("id");

        var result = evm.applySetting(settings, cb).getResultList();
        return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
    }

    @Transactional
    public boolean assign(UUID siteId, UUID patientId) {
        var patSettings = EntityViewSetting.create(Patient.IdView.class);
        var patCb = cbf.create(em, Patient.class).where("id").eq(patientId);

        var orgSettings = EntityViewSetting.create(Organisation.IdView.class);
        var orgCb = cbf.create(em, Organisation.class).where("id").eq(siteId);
        try {
            var patient = evm.applySetting(patSettings, patCb).getSingleResult();
            var organisation = evm.applySetting(orgSettings, orgCb).getSingleResult();
            var assignment = evm.create(SiteAssignment.View.class);

            assignment.setPatient(patient);
            assignment.setSite(organisation);
            evm.save(em, assignment);
            return true;
        } catch (Exception e) {
            throw new BadRequestException("One or more records not found");
        }
    }

    @Transactional
    public boolean unassign(UUID patientId) {
        siteAssignmentRepository.deleteByPatientId(patientId);
        return true;
    }
}
