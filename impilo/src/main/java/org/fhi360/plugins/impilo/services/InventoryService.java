package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * The InventoryService class is a Java service that lists stock issuances for a specific site using entity views and
 * criteria queries.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class InventoryService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    /**
     * The function returns a list of stock issuances for a given outlet ID.
     *
     * @param outletId The `outletId` parameter is a UUID (Universally Unique Identifier) that represents the ID of an
     * outlet.
     * @return The method is returning a List of objects of type StockIssuance.View.
     */
    public List<StockIssuance.View> list(UUID outletId) {
        var settings = EntityViewSetting.create(StockIssuance.View.class);
        var cb = cbf.create(em, StockIssuance.class)
            .where("site.id").eq(outletId);

        return evm.applySetting(settings, cb).getResultList();
    }
}
