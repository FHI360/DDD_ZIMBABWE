package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.fhi360.plugins.impilo.domain.entities.Stock;


/**
 * The RefillBarcodeSubqueryProvider class is a Java class that  provides a
 * subquery to retrieve the barcode for a refill.
 */
public class RefillBarcodeSubqueryProvider implements SubqueryProvider {
    @Override
    public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
        //@formatter:off
        return subqueryBuilder.from(Stock.class, "s")
            .select("batchNo")
                .where("s.batchIssuanceId").eqExpression("EMBEDDING_VIEW(batchIssuanceId)")
            .orderBy("date", false)
            .setMaxResults(1)
            .end();
        //@formatter:on
    }
}
