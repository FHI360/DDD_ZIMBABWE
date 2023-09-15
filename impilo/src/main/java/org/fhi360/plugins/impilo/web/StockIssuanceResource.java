package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.fhi360.plugins.impilo.services.StockIssuanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * The StockIssuanceResource class is a REST controller that handles requests related to stock issuance, including
 * retrieving, listing, saving, updating, and deleting stock issuances.
 */
@RestController
@RequestMapping("/api/impilo/stock-issuance")
@RequiredArgsConstructor
public class StockIssuanceResource {
    private final StockIssuanceService issuanceService;

    /**
     * The function retrieves a stock issuance by its ID.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that is used to identify a specific stock
     * issuance.
     * @return The method is returning an object of type StockIssuance.View.
     */
    @GetMapping("/{id}")
    public StockIssuance.View getById(@PathVariable UUID id) {
        return issuanceService.getById(id);
    }

    /**
     * The function returns a paged result of stock issuances based on the provided keyword, start index, and page size.
     *
     * @param keyword The "keyword" parameter is an optional parameter that allows you to search for stock issuances based
     * on a specific keyword. If you provide a value for this parameter, the list method will return stock issuances that
     * match the keyword.
     * @param start The "start" parameter is used to specify the starting index of the list of stock issuances that you
     * want to retrieve. It is an optional parameter and its default value is 0, which means that if no value is provided,
     * the list will start from the first element.
     * @param pageSize The `pageSize` parameter is used to specify the number of items to be displayed per page in the
     * returned result. By default, if no value is provided, it is set to 10.
     * @return The method is returning a PagedResult object containing a list of StockIssuance.View objects.
     */
    @GetMapping
    public PagedResult<StockIssuance.View> list(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false, defaultValue = "0") int start,
                                                @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return issuanceService.list(keyword, start, pageSize);
    }

    /**
     * The function "availableStock" returns a list of available stock items.
     *
     * @return The method is returning a list of objects of type Stock.View.
     */
    @GetMapping("/available-stock")
    public List<Stock.View> availableStock() {
        return issuanceService.listAvailableStock();
    }

    /**
     * The above function is a POST request handler that saves a new stock issuance record and returns the saved record.
     *
     * @param stock The "stock" parameter is of type StockIssuance.CreateView and is annotated with @RequestBody and
     * @Valid. It represents the data that is sent in the request body and is used to create a new stock issuance.
     * @return The method is returning a StockIssuance.View object.
     */
    @PostMapping
    public StockIssuance.View save(@RequestBody @Valid StockIssuance.CreateView stock) {
        return issuanceService.save(stock);
    }

    /**
     * The above function updates a stock issuance record and returns the updated record.
     *
     * @param stock The "stock" parameter is of type StockIssuance.CreateView and is annotated with @RequestBody and
     * @Valid. This means that it is expected to be passed in the request body and will be validated against any validation
     * constraints specified in the StockIssuance.CreateView class.
     * @return The method is returning an object of type StockIssuance.View.
     */
    @PutMapping
    public StockIssuance.View update(@RequestBody @Valid StockIssuance.CreateView stock) {
        return issuanceService.save(stock);
    }

    /**
     * The above function is a DELETE request mapping that deletes an object by its ID.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that represents the unique identifier of the
     * resource to be deleted.
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        issuanceService.deleteById(id);
    }
}
