package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;

/**
 * The StockIssuedSubqueryProvider class provides a subquery that calculates the sum of bottles for a given stock ID.
 */
public class StockIssuedSubqueryProvider implements SubqueryProvider {
    @Override
    public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
        //@formatter:off
        return subqueryBuilder.from(StockIssuance.class, "i")
            .select("sum(bottles)")
                .where("i.stock.id").eqExpression("EMBEDDING_VIEW(id)")
            .end();
        //@formatter:on
    }
}
