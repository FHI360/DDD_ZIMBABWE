package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.services.ExtensionService;
import io.github.jbella.snl.core.api.services.errors.RecordNotFoundException;
import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.fhi360.plugins.impilo.domain.repositories.StockIssuanceRepository;
import org.fhi360.plugins.impilo.domain.repositories.StockRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * The StockIssuanceService class is a Java service that provides methods for saving, retrieving, and deleting stock
 * issuances, as well as listing available stock.
 */
@Service
@PreAuthorize("hasRole('ROLE_USER')")
@RequiredArgsConstructor
public class StockIssuanceService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final StockIssuanceRepository stockIssuanceRepository;
    private final StockRepository stockRepository;
    private final ExtensionService extensionService;

    /**
     * The function saves a stock issuance record, generates a reference if it doesn't exist, sets the synced flag to
     * false, saves the record to the database, and returns the saved record.
     *
     * @param stock The "stock" parameter is an object of type StockIssuance.CreateView, which contains the data needed to
     *              create a new stock issuance.
     * @return The method is returning a StockIssuance.View object.
     */
    @Transactional
    public StockIssuance.View save(StockIssuance.CreateView stock) {
        if (stock.getId() == null) {
            stock.setReference(UUID.randomUUID());
        }
        stock.setSynced(false);
        stockRepository.findById(stock.getStock().getId()).ifPresent(s -> stock.setBatchIssuanceId(s.getBatchIssuanceId()));
        evm.save(em, stock);
        //This done to enable Javers auditing kick in
        stockIssuanceRepository.findById(stock.getId()).ifPresent(stockIssuanceRepository::save);
        return getById(stock.getId());
    }

    /**
     * The function retrieves a StockIssuance view by its ID and throws a RecordNotFoundException if no record is found.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that is used to identify a specific stock
     *           issuance record.
     * @return The method is returning an instance of the StockIssuance.View class.
     */
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

    /**
     * The function deletes a stock issuance record by its ID using the EntityManager's remove method.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that represents the unique identifier of the
     *           object to be deleted.
     */
    @Transactional
    public void deleteById(UUID id) {
        evm.remove(em, StockIssuance.View.class, id);
    }

    /**
     * The function `list` retrieves a paged list of `StockIssuance` objects based on a keyword search and organization
     * hierarchy.
     *
     * @param keyword The "keyword" parameter is a string that represents a search term or phrase. It is used to filter the
     *                results based on certain criteria.
     * @param start   The "start" parameter is the index of the first item to be retrieved from the list. It is used for
     *                pagination purposes, allowing you to retrieve a subset of the list starting from a specific index.
     * @param size    The "size" parameter represents the number of items to be returned per page in the paged result.
     * @return The method is returning a PagedResult object containing a list of StockIssuance.View objects.
     */
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
        var hierarchy = extensionService.getPriorityExtension(AuthenticationServiceExtension.class)
            .getOrganisationHierarchy();
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("FACILITY")) {
                cb.where("stock.facility.id").eq(hierarchy.get(0).getId());
            }
        }

        cb.orderByAsc("stock.regimen")
            .orderByAsc("stock.batchNo")
            .orderByAsc("id");

        var result = evm.applySetting(settings, cb).getResultList();
        return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
    }

    /**
     * This function lists the available stock items, taking into account the current user information and ordering the
     * results by regimen, batch number, and ID.
     *
     * @return The method is returning a List of objects of type Stock.View.
     */
    public List<Stock.View> listAvailableStock() {
        var settings = EntityViewSetting.create(Stock.View.class);
        var cb = cbf.create(em, Stock.class);

        var hierarchy = extensionService.getPriorityExtension(AuthenticationServiceExtension.class)
            .getOrganisationHierarchy();
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("FACILITY")) {
                cb.where("facility.id").eq(hierarchy.get(0).getId());
            }
        }

        cb.orderByAsc("regimen")
            .orderByAsc("batchNo")
            .orderByAsc("id");

        return evm.applySetting(settings, cb).getResultList();
    }
}
