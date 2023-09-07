package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.fhi360.plugins.impilo.domain.entities.StockRequest;
import org.fhi360.plugins.impilo.web.models.ARVRequest;
import org.fhi360.plugins.impilo.web.models.StockFulfill;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class StockRequestService {
    private final CriteriaBuilderFactory cbf;
    private final EntityManager em;
    private final EntityViewManager evm;

    public Optional<StockRequest.CreateView> getById(UUID id) {
        var settings = EntityViewSetting.create(StockRequest.CreateView.class);
        var cb = cbf.create(em, StockRequest.class)
            .where("id").eq(id);
        try {
            var result = evm.applySetting(settings, cb).getSingleResult();
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }

    }

    @Transactional
    public boolean saveRequest(LocalDate date, UUID siteId, String requestId, ARVRequest request) {
        var settings = EntityViewSetting.create(Organisation.ShortView.class);
        var cb = cbf.create(em, Organisation.class)
            .where("id").eq(siteId);
        try {
            var organisation = evm.applySetting(settings, cb).getSingleResult();
            var stockRequest = evm.create(StockRequest.CreateView.class);
            stockRequest.setSite(organisation);
            stockRequest.setBottles(request.getBottles());
            stockRequest.setArvDrug(request.getArvDrug());
            stockRequest.setDate(date);
            stockRequest.setRequestId(requestId);
            stockRequest.setReference(UUID.randomUUID());
            evm.save(em, stockRequest);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public PagedResult<StockRequest.CreateView> list(UUID siteId, String arvDrug, int start, int pageSize) {
        int offset = start * pageSize;
        var settings = EntityViewSetting.create(StockRequest.CreateView.class, offset, pageSize);
        var cb = cbf.create(em, StockRequest.class);

        if (siteId != null) {
            cb.where("site.id").eq(siteId);
        }
        if (StringUtils.isNotBlank(arvDrug)) {
            cb.where("arvDrug").eq(arvDrug);
        }

        cb.orderByAsc("date").orderByAsc("id");

        var result = evm.applySetting(settings, cb).getResultList();
        return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
    }

    public List<StockFulfill> fulfill(UUID siteId) {
        List<StockFulfill> result = new ArrayList<>();

        var settings = EntityViewSetting.create(StockIssuance.View.class);
        var cb = cbf.create(em, StockIssuance.class)
            .where("site.id").eq(siteId);

        var issuance = evm.applySetting(settings, cb).getResultList();
        issuance.forEach(i -> {
            StockFulfill fulfill = new StockFulfill();
            fulfill.setBatchNo(i.stock().batchNo());
            fulfill.setBarcode(i.stock().batchNo());
            fulfill.setExpirationDate(i.stock().expirationDate());
            fulfill.setBottles(i.bottles());
            fulfill.setRegimen(i.stock().regimen());

            result.add(fulfill);
        });

        return result;
    }
}
