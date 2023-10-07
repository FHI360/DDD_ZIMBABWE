package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.ExtensionService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.fhi360.plugins.impilo.domain.entities.StockRequest;
import org.fhi360.plugins.impilo.services.models.ActivationData;
import org.fhi360.plugins.impilo.services.models.StockFulfill;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * The `SiteActivationService` class is a Java service that activates an outlet by retrieving relevant data from the database
 * and returning it in an `ActivationData` object.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class SiteActivationService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final ExtensionService extensionService;
    private final StockRequestService stockRequestService;

    /**
     * The `activate()` function retrieves data from the database and returns an `ActivationData` object containing
     * information about the organization and its associated patients.
     *
     * @return The method `activate()` returns an object of type `ActivationData`.
     */
    public ActivationData activate() {
        Organisation.IdView org = extensionService.getPriorityExtension(AuthenticationServiceExtension.class)
            .organisation();
        var settings = EntityViewSetting.create(Organisation.ShortView.class);
        var cb = cbf.create(em, Organisation.class)
            .where("id").eq(org.getId());
        var organisations = evm.applySetting(settings, cb).getResultList();
        if (organisations.isEmpty()) {
            return new ActivationData();
        }
        var organisation = organisations.get(0);
        ActivationData data = new ActivationData();

        data.setSite(organisation.getName());
        var settings1 = EntityViewSetting.create(Patient.View.class);
        var cb1 = cbf.create(em, Patient.class, "p");
        //@formatter:off
        cb1.whereExists()
            .from(Devolve.class, "h")
                .select("1")
                    .whereExpression("patient.id = p.id")
                    .where("organisation.id").eq(organisation.getId())
                    .whereOr()
                        .where("reasonDiscontinued").isNull()
                        .where("LENGTH(reasonDiscontinued)").eq(0)
                    .endOr()
                    .where("date").eq()
                        .from(Devolve.class, "d")
                            .select("MAX(date)")
                                .whereExpression("d.patient.id = p.id")
                        .end()
            .end();
            //@formatter:on
        var patients = evm.applySetting(settings1, cb1).getResultList();
        data.setPatients(patients);

        List<Refill.SyncView> dispenses = patients.stream()
            .flatMap(patient -> getDispenses(patient.id()).stream())
            .toList();
        data.setRefills(dispenses);

        List<StockFulfill> stockFulfills = stockRequestService.fulfill(org.getId());
        data.setStockIssues(stockFulfills);

        List<StockRequest.SyncView> requests = getRequest(org.getId());
        data.setRequests(requests);

        return data;
    }

    private List<Refill.SyncView> getDispenses(UUID patientId) {
        var settings = EntityViewSetting.create(Refill.SyncView.class, 0, 3);
        var cb = cbf.create(em, Refill.class)
            .where("patient.id").eq(patientId)
            .orderByDesc("date")
            .orderByDesc("id");

        return evm.applySetting(settings, cb).getResultList();
    }

    private List<StockRequest.SyncView> getRequest(UUID siteId) {
        var settings = EntityViewSetting.create(StockRequest.SyncView.class, 0, 50);
        var cb = cbf.create(em, StockRequest.class)
            .where("site.id").eq(siteId)
            .orderByDesc("date")
            .orderByDesc("id");

        return evm.applySetting(settings, cb).getResultList();
    }
}
