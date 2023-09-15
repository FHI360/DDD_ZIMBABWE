package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;

/**
 * The StockFulfilledSubqueryProvider class is a Java class that provides a
 * subquery to retrieve the total stock issued per request.
 */
public class StockFulfilledSubqueryProvider implements SubqueryProvider {
    @Override
    public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
        //@formatter:off
        return subqueryBuilder.from(StockIssuance.class, "r")
            .select("SUM(bottles)")
                .where("r.request.id").eqExpression("EMBEDDING_VIEW(id)")
            .end();
        //@formatter:on
    }
}
