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
import org.fhi360.plugins.impilo.services.models.ARVRequest;
import org.fhi360.plugins.impilo.services.models.StockFulfill;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The StockRequestService class is a Java service that handles stock requests, including retrieving, saving, listing, and
 * fulfilling requests.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class StockRequestService {
    private final CriteriaBuilderFactory cbf;
    private final EntityManager em;
    private final EntityViewManager evm;

    /**
     * The function retrieves a stock request by its ID and returns it wrapped in an Optional, or returns an empty Optional
     * if the request does not exist.
     *
     * @param id The "id" parameter is of type UUID, which stands for Universally Unique Identifier. It is a 128-bit value
     * used to uniquely identify an object or entity in a system. In this case, it is used to identify a specific
     * StockRequest object.
     * @return The method is returning an Optional object that contains an instance of the StockRequest.CreateView class.
     */
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

    /**
     * The function saves a stock request with the given parameters and returns true if successful, false otherwise.
     *
     * @param date The date parameter is of type LocalDate and represents the date of the request.
     * @param siteId The `siteId` parameter is a UUID (Universally Unique Identifier) that represents the ID of a site or
     * organization.
     * @param requestId The `requestId` parameter is a unique identifier for the request. It is used to distinguish one
     * request from another.
     * @param request The "request" parameter is an object of type ARVRequest.
     * @return The method is returning a boolean value. If the try block is executed successfully, it will return true. If
     * an exception occurs, it will return false.
     */
    @Transactional
    public boolean saveRequest(LocalDateTime date, UUID siteId, UUID requestId, ARVRequest request) {
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
            stockRequest.setRequestId(requestId.toString());
            stockRequest.setReference(requestId);
            evm.save(em, stockRequest);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This Java function retrieves a paged list of stock requests based on site ID and ARV drug, with sorting by date and
     * ID.
     *
     * @param siteId The siteId parameter is a UUID (Universally Unique Identifier) that represents the ID of a site.
     * @param arvDrug The "arvDrug" parameter is a String that represents the name or identifier of a drug. It is used to
     * filter the stock requests based on the specified drug.
     * @param start The start parameter is the index of the first item to be returned in the result set. It is used for
     * pagination purposes.
     * @param pageSize The pageSize parameter determines the number of items to be displayed per page in the paged result.
     * @return The method is returning a PagedResult object containing a list of StockRequest.CreateView objects.
     */
    public PagedResult<StockRequest.ListView> list(UUID siteId, String arvDrug, int start, int pageSize) {
        int offset = start * pageSize;
        var settings = EntityViewSetting.create(StockRequest.ListView.class, offset, pageSize);
        var cb = cbf.create(em, StockRequest.class);

        if (siteId != null) {
            cb.where("site.id").eq(siteId);
        }
        if (StringUtils.isNotBlank(arvDrug)) {
            cb.where("arvDrug").eq(arvDrug);
        }

        cb.orderByDesc("date").orderByAsc("id");

        var result = evm.applySetting(settings, cb).getResultList();
        return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
    }

    /**
     * The fulfill() function retrieves stock issuances for a given site and creates a list of StockFulfill objects
     * containing relevant information from each issuance.
     *
     * @param siteId The siteId parameter is a UUID (Universally Unique Identifier) that represents the ID of a site.
     * @return The method `fulfill` returns a `List` of `StockFulfill` objects.
     */
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
            fulfill.setBalance(i.balance());
            fulfill.setRegimen(i.stock().regimen());
            fulfill.setReference(i.reference());
            fulfill.setRequestReference(i.requestReference());
            fulfill.setBatchIssueId(i.batchIssuanceId());
            fulfill.setAcknowledged(i.acknowledged());

            result.add(fulfill);
        });

        return result;
    }
}
