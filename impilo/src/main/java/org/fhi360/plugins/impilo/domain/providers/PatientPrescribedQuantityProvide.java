package org.fhi360.plugins.impilo.domain.providers;

import com.blazebit.persistence.SubqueryInitiator;
import com.blazebit.persistence.view.SubqueryProvider;
import org.fhi360.plugins.impilo.domain.entities.Prescription;

/**
 * The PatientPrescribedQuantityProvide class is a Java class that  provides a
 * subquery to retrieve the prescribed dispense quantity for a given patient.
 */
public class PatientPrescribedQuantityProvide implements SubqueryProvider {
    @Override
    public <T> T createSubquery(SubqueryInitiator<T> subqueryBuilder) {
        //@formatter:off
        return subqueryBuilder.from(Prescription.class, "r")
            .select("prescribedQty")
                .where("r.patient.id").eqExpression("EMBEDDING_VIEW(id)")
            .orderBy("time", false)
            .setMaxResults(1)
            .end();
        //@formatter:on
    }
}
