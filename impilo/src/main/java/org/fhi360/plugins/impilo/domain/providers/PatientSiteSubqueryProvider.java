package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.SiteAssignment;

public class PatientSiteSubqueryProvider implements SubqueryProvider {
    @Override
    public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
        //@formatter:off
        return subqueryBuilder.from(SiteAssignment.class, "s")
            .select("site.name")
                .where("s.patient.id").eqExpression("EMBEDDING_VIEW(id)")
            .end();
        //@formatter:on
    }
}
