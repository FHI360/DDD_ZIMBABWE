package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.errors.BadRequestException;
import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class DevolveService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    public PagedResult<Patient.ListView> list(String keyword, Boolean assigned, int start, int size) {
        var settings = EntityViewSetting.create(Patient.ListView.class, start * size, size);

        var cb = cbf.create(em, Patient.class);

        try {
            if (StringUtils.isNotBlank(keyword)) {
                keyword = "%" + StringUtils.trim(keyword) + "%";
                //@formatter:off
                cb.whereOr()
                    .where("givenName").like(false).value(keyword).noEscape()
                    .where("familyName").like(false).value(keyword).noEscape()
                    .where("hospitalNumber").like(false).value(keyword).noEscape()
                .endOr();
                //@formatter:on
            }
            if (Objects.nonNull(assigned)) {
                if (assigned) {
                    //@formatter:off
                    cb.joinOn(Devolve.class, "a", JoinType.INNER)
                        .onExpression("id = a.patient.id")
                    .end();
                    //@formatter:on
                }
                if (!assigned) {
                    //@formatter:off
                    cb.joinOn(Devolve.class, "b", JoinType.LEFT)
                        .onExpression("id = b.patient.id")
                    .end()
                    .where("b.id").isNull();
                    //@formatter:on
                }
            }

            cb.orderByAsc("givenName")
                .orderByDesc("familyName")
                .orderByAsc("id");

            var result = evm.applySetting(settings, cb).getResultList();
            return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public boolean devolve(UUID organisationId, UUID patientId) {
        var patSettings = EntityViewSetting.create(Patient.IdView.class);
        var patCb = cbf.create(em, Patient.class).where("id").eq(patientId);

        var orgSettings = EntityViewSetting.create(Organisation.IdView.class);
        var orgCb = cbf.create(em, Organisation.class).where("id").eq(organisationId);
        try {
            var patient = evm.applySetting(patSettings, patCb).getSingleResult();
            var organisation = evm.applySetting(orgSettings, orgCb).getSingleResult();
            var devolve = evm.create(Devolve.CreateView.class);

            devolve.setPatient(patient);
            devolve.setOrganisation(organisation);
            devolve.setDate(LocalDateTime.now());
            devolve.setReference(UUID.randomUUID());
            devolve.setSynced(false);
            evm.save(em, devolve);
            return true;
        } catch (Exception e) {
            throw new BadRequestException("One or more records not found");
        }
    }

    @Transactional
    public boolean unassign(UUID patientId) {
        var settings = EntityViewSetting.create(Devolve.CreateView.class, 0, 1);
        var cb = cbf.create(em, Devolve.class)
            .where("patient.id").eq(patientId)
            .orderByDesc("date")
            .orderByDesc("id");
        var devolves = evm.applySetting(settings, cb).getResultList();
        if (!devolves.isEmpty()) {
            var last = devolves.get(0);
            var devolve = evm.create(Devolve.CreateView.class);
            devolve.setPatient(last.getPatient());
            devolve.setOrganisation(last.getOrganisation());
            devolve.setDate(LocalDateTime.now());
            devolve.setReasonDiscontinued("Unspecified");
            devolve.setSynced(false);
            devolve.setReference(UUID.randomUUID());

            evm.save(em, devolve);
        }
        return true;
    }
}
