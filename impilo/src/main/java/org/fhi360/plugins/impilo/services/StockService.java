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
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * The `StockService` class is a Java service class that provides methods for saving, retrieving, and deleting stock data,
 * as well as listing stock items based on certain criteria.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class StockService {
    private final EntityViewManager evm;
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final ExtensionService extensionService;

    /**
     * The function saves a stock item with a generated reference and returns the saved stock item.
     *
     * @param stock The "stock" parameter is an object of type Stock.CreateView.
     * @return The method is returning a Stock.View object.
     */
    @Transactional
    public Stock.View save(Stock.CreateView stock) {
        var facility = extensionService.getPriorityExtension(AuthenticationServiceExtension.class).organisation();
        stock.setFacility(facility);
        stock.setReference(UUID.randomUUID());
        evm.save(em, stock);
        return getById(stock.getId());
    }

    /**
     * The function retrieves a stock view by its ID and throws an exception if no stock is found.
     *
     * @param id The "id" parameter is of type UUID, which stands for Universally Unique Identifier. It is a 128-bit value
     * used to uniquely identify an entity in the system. In this case, it is used to find a stock entity by its unique
     * identifier.
     * @return The method is returning an instance of the Stock.View class.
     */
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

    /**
     * The function `applicableRegimen()` retrieves a list of distinct regimen names from the `Patient` entity, filtered by
     * the organization hierarchy if it is not empty and only contains a single facility.
     *
     * @return The method `applicableRegimen()` returns a `List<String>`.
     */
    public List<String> applicableRegimen() {
        var cb = cbf.create(em, Tuple.class).from(Patient.class)
            .distinct()
            .select("regimen");

        var hierarchy = extensionService.getPriorityExtension(AuthenticationServiceExtension.class)
            .getOrganisationHierarchy();
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("FACILITY")) {
                cb.where("organisation.id").eq(hierarchy.get(0).getId());
            }
        }
        return cb.getResultList()
            .stream().map(tuple -> tuple.get(0, String.class))
            .toList();
    }

    /**
     * The function deletes a stock entity by its ID using the EntityManager's remove method.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that represents the unique identifier of the
     * stock entity that needs to be deleted.
     */
    @Transactional
    public void deleteById(UUID id) {
        evm.remove(em, Stock.View.class, id);
    }

    /**
     * The function `list` retrieves a paged list of stock items based on a keyword search, start index, and page size,
     * with additional filtering based on the user's organization hierarchy.
     *
     * @param keyword The "keyword" parameter is used to search for stocks that match a specific keyword. It is a string
     * that represents the keyword to search for.
     * @param start The "start" parameter is the index of the first item to be retrieved from the list. It is used to
     * determine the starting position of the query results.
     * @param size The "size" parameter represents the number of items to be returned per page in the paged result.
     * @return The method is returning a PagedResult object containing a list of Stock.View objects.
     */
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
        var hierarchy = extensionService.getPriorityExtension(AuthenticationServiceExtension.class)
            .getOrganisationHierarchy();
        if (!hierarchy.isEmpty()) {
            if (hierarchy.size() == 1 && hierarchy.get(0).getType().equals("FACILITY")) {
                cb.where("facility.id").eq(hierarchy.get(0).getId());
            }
        }

        cb.orderByDesc("date")
            .orderByAsc("regimen")
            .orderByAsc("batchNo")
            .orderByAsc("id");

        var result = evm.applySetting(settings, cb).getResultList();
        return new PagedResult<>(result, result.getTotalSize(), result.getTotalPages());
    }
}
