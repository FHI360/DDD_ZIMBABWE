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
import org.fhi360.plugins.impilo.web.models.ActivationData;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class SiteActivationService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final ExtensionService extensionService;

    public ActivationData activate() {
        try {
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
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
