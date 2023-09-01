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

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class InventoryService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;

    public List<StockIssuance.View> list(UUID siteId) {
        var settings = EntityViewSetting.create(StockIssuance.View.class);
        var cb = cbf.create(em, StockIssuance.class)
            .where("site.id").eq(siteId);

        return evm.applySetting(settings, cb).getResultList();
    }
}
