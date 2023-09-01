package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.Devolve;

public class PatientSiteNameSubqueryProvider implements SubqueryProvider {
    @Override
    public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
        //@formatter:off
        return subqueryBuilder.from(Devolve.class, "s")
            .select("organisation.name")
                .where("s.patient.id").eqExpression("EMBEDDING_VIEW(id)")
                .whereOr()
                    .where("reasonDiscontinued").isNull()
                    .where("LENGTH(reasonDiscontinued)").eq(0)
                .endOr()
                .where("date").eq()
                    .from(Devolve.class, "d")
                        .select("MAX(date)")
                            .whereExpression("d.patient.id = EMBEDDING_VIEW(id)")
                    .end()
                .orderByDesc("date")
                .setMaxResults(1)
            .end();
        //@formatter:on
    }
}
