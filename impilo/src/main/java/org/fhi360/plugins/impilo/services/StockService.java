package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import io.github.jbella.snl.core.api.services.errors.RecordNotFoundException;
import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.pf4j.PluginManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StockService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final PluginManager pluginManager;
    private final TransactionHandler transactionHandler;

    public Stock.View save(Stock.CreateView stock) {
        var hierarchy = pluginManager.getExtensions(AuthenticationServiceExtension.class).stream()
            .map(AuthenticationServiceExtension::getOrganisationHierarchy).findFirst().orElse(new ArrayList<>());
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("Facility")) {
                stock.setFacility(hierarchy.get(0));
            }
        }
        transactionHandler.runInTransaction(() -> {
            evm.save(em, stock);
            return null;
        });
        return getById(stock.getId());
    }

    public Stock.View getById(UUID id) {
        var settings = EntityViewSetting.create(Stock.View.class);
        var cb = cbf.create(em, Stock.class)
            .where("id").eq(id);

        try {
            return evm.applySetting(settings, cb).getSingleResult();
        } catch (NoResultException e) {
            throw new RecordNotFoundException("No stock with id " + id + " found");
        }
    }

    public List<String> applicableRegimen() {
        var cb = cbf.create(em, Tuple.class).from(Patient.class)
            .distinct()
            .select("regimen");

        var hierarchy = pluginManager.getExtensions(AuthenticationServiceExtension.class).stream()
            .map(AuthenticationServiceExtension::getOrganisationHierarchy).findFirst().orElse(new ArrayList<>());
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("Facility")) {
                cb.where("facility.id").eq(hierarchy.get(0).getId());
            }
        }
        return cb.getResultList()
            .stream().map(tuple -> tuple.get(0, String.class))
            .toList();
    }

    @Transactional
    public void deleteById(UUID id) {
        evm.remove(em, Stock.View.class, id);
    }

    public PagedResult<Stock.View> list(String keyword, int start, int size) {
        var settings = EntityViewSetting.create(Stock.View.class, start * size, size);
        var cb = cbf.create(em, Stock.class);
        if (StringUtils.isNotBlank(keyword)) {
            keyword = "%" + StringUtils.trim(keyword) + "%";
            //@formatter:off
            cb.whereOr()
                .where("regimen").like(false).value(keyword).noEscape()
                .where("batchNo").like(false).value(keyword).noEscape()
                .where("serialNo").like(false).value(keyword).noEscape()
            .endOr();
            //@formatter:on
        }
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

        var result = evm.applySetting(settings, cb).getResultList();
        return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
    }
}
