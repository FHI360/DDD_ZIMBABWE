package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.services.errors.RecordNotFoundException;
import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.pf4j.PluginManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class StockIssuanceService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final PluginManager pluginManager;

    @Transactional
    public StockIssuance.View save(StockIssuance.CreateView stock) {
        evm.save(em, stock);
        return getById(stock.getId());
    }

    public StockIssuance.View getById(UUID id) {
        var settings = EntityViewSetting.create(StockIssuance.View.class);
        var cb = cbf.create(em, StockIssuance.class)
            .where("id").eq(id);

        try {
            return evm.applySetting(settings, cb).getSingleResult();
        } catch (NoResultException e) {
            throw new RecordNotFoundException("No stock issuance with id " + id + " found");
        }
    }

    @Transactional
    public void deleteById(UUID id) {
        evm.remove(em, StockIssuance.View.class, id);
    }

    public PagedResult<StockIssuance.View> list(String keyword, int start, int size) {
        var settings = EntityViewSetting.create(StockIssuance.View.class, start * size, size);
        var cb = cbf.create(em, StockIssuance.class);
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + StringUtils.trim(keyword) + "%";
            //@formatter:off
            cb.whereOr()
                .where("stock.regimen").like(false).value(keyword).noEscape()
                .where("stock.batchNo").like(false).value(keyword).noEscape()
                .where("stock.serialNo").like(false).value(keyword).noEscape()
            .endOr();
            //@formatter:on
        }
        var hierarchy = pluginManager.getExtensions(AuthenticationServiceExtension.class).stream()
            .map(AuthenticationServiceExtension::getOrganisationHierarchy).findFirst().orElse(new ArrayList<>());
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("Facility")) {
                cb.where("stock.facility.id").eq(hierarchy.get(0).getId());
            }
        }

        cb.orderByAsc("stock.regimen")
            .orderByAsc("stock.batchNo")
            .orderByAsc("id");

        var result = evm.applySetting(settings, cb).getResultList();
        return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
    }

    public List<Stock.View> listAvailableStock() {
        var settings = EntityViewSetting.create(Stock.View.class);
        var cb = cbf.create(em, Stock.class);

        var hierarchy = pluginManager.getExtensions(AuthenticationServiceExtension.class).stream()
            .map(AuthenticationServiceExtension::getOrganisationHierarchy).findFirst().orElse(new ArrayList<>());
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("Facility")) {
                cb.where("facility.id").eq(hierarchy.get(0).getId());
            }
        }

        cb.orderByAsc("regimen")
            .orderByAsc("batchNo")
            .orderByAsc("id");

        return evm.applySetting(settings, cb).getResultList();
    }
}
