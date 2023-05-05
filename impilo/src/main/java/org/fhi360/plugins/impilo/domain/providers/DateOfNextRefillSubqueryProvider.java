package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.Refill;

public class DateOfNextRefillSubqueryProvider implements SubqueryProvider {
        @Override
        public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
            //@formatter:off
            return subqueryBuilder.from(Refill.class, "r")
                    .select("dateNextRefill")
                        .where("r.patient.id").eqExpression("EMBEDDING_VIEW(id)")
                        .orderBy("date", false)
                        .setMaxResults(1)
                    .end();
            //@formatter:on
        }
    }
