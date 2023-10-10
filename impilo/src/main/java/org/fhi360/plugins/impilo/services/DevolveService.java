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
import java.util.UUID;

/**
 * The `DevolveService` class is a Java service class that provides methods for listing patients, devolving patients to an
 * organization, and un-devolving patients.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class DevolveService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    /**
     * The `list` function retrieves a paged list of patients based on a keyword, devolved status, start index, and page
     * size.
     *
     * @param keyword  The keyword parameter is used to search for patients based on their given name, family name, or
     *                 hospital number. It is a string that represents the search term.
     * @param devolved The "devolved" parameter is a boolean value that indicates whether to filter the patients based on
     *                 their devolved status. If it is set to true, only patients who have been devolved will be included in the result. If
     *                 it is set to false, only patients who have not been devolved
     * @param start    The "start" parameter is the index of the first item to be retrieved from the list. It is used for
     *                 pagination purposes, allowing you to retrieve a subset of the total list of patients.
     * @param size     The "size" parameter determines the number of items to be returned per page in the paged result.
     * @return The method is returning a PagedResult object.
     */
    public PagedResult<Patient.ListView> list(String keyword, Boolean devolved, int start, int size) {
        var settings = EntityViewSetting.create(Patient.ListView.class, start * size, size);

        var cb = cbf.create(em, Patient.class, "p");

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
        if (devolved != null) {
            if (devolved) {
                //@formatter:off
                cb.joinOn(Devolve.class, "a", JoinType.INNER)
                    .onExpression("id = a.patient.id")
                .end()
                .where("a.date").eq()
                    .from(Devolve.class, "d")
                        .select("MAX(date)")
                            .whereExpression("a.patient.id = p.id")
                            .where("reasonDiscontinued").isNull()
                    .end();
                //@formatter:on
            }
            if (!devolved) {
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
    }

    /**
     * The `devolve` function takes in an organization ID and a patient ID, retrieves the corresponding patient and
     * organization entities from the database, creates a new `Devolve` entity with the patient, organization, current
     * date, and a random reference, and saves it to the database.
     *
     * @param organisationId The `organisationId` parameter is a unique identifier (UUID) that represents the ID of the
     *                       organization to which the patient belongs.
     * @param patientId      The patientId parameter is a unique identifier for a specific patient.
     * @return The method is returning a boolean value. It returns true if the devolve operation is successful, and false
     * otherwise.
     */
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

    /**
     * The function unDevolve retrieves the last devolve record for a given patient, creates a new devolve record with the
     * same patient and organisation, sets the current date and other default values, and saves it to the database.
     *
     * @param patientId The patientId parameter is a unique identifier (UUID) for a patient.
     * @return The method is returning a boolean value of true.
     */
    @Transactional
    public boolean unDevolve(UUID patientId) {
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
