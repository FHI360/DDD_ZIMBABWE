package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.ClinicData;

/**
 * The LastClinicDateSubqueryProvider class provides a subquery that retrieves the last clinic date for a given patient.
 */
public class LastClinicDateSubqueryProvider implements SubqueryProvider {
    @Override
    public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
        //@formatter:off
            return subqueryBuilder.from(ClinicData.class, "r")
                    .select("date")
                        .where("r.patient.id").eqExpression("EMBEDDING_VIEW(id)")
                    .orderBy("date", false)
                    .setMaxResults(1)
                    .end();
            //@formatter:on
    }
}
