package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.fhi360.plugins.impilo.services.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * The StockResource class is a REST controller that handles CRUD operations for stocks and provides additional endpoints
 * for retrieving applicable regimens.
 */
@RestController
@RequestMapping("/api/impilo/stocks")
@RequiredArgsConstructor
public class StockResource {
    private final StockService stockService;

    /**
     * The function retrieves a stock by its ID and returns a view of the stock.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that is used to identify a specific stock.
     * @return The method is returning a Stock.View object.
     */
    @GetMapping("/{id}")
    public Stock.View getById(@PathVariable UUID id) {
        return stockService.getById(id);
    }

    /**
     * The function is a GET request handler that returns a paged list of stock items based on the provided keyword, start
     * index, and page size.
     *
     * @param keyword The "keyword" parameter is an optional parameter that allows you to search for stocks based on a
     * specific keyword. If provided, the stocks returned will be filtered based on this keyword.
     * @param start The "start" parameter is used to specify the starting index of the list of stocks to be returned. It is
     * an optional parameter and has a default value of 0 if not provided.
     * @param pageSize The `pageSize` parameter is used to specify the number of items to be displayed per page in the
     * returned result. By default, if no value is provided for `pageSize`, it will be set to 10.
     * @return The method is returning a PagedResult object containing a list of Stock.View objects.
     */
    @GetMapping
    public PagedResult<Stock.View> list(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return stockService.list(keyword, start, pageSize);
    }

    /**
     * The above function is a POST request handler that saves a new stock item and returns its view representation.
     *
     * @param stock The stock parameter is of type Stock.CreateView, which is a view class used for creating a new stock
     * object. It is annotated with @RequestBody to indicate that the parameter should be obtained from the request body.
     * It is also annotated with @Valid to enable validation of the stock object based on the validation
     * @return The method is returning a Stock.View object.
     */
    @PostMapping
    public Stock.View save(@RequestBody @Valid Stock.CreateView stock) {
        return stockService.save(stock);
    }

    /**
     * The above function updates a stock record using the provided data and returns the updated stock record.
     *
     * @param stock The "stock" parameter is of type Stock.CreateView, which is a view class used for creating a new stock
     * object. It is annotated with @RequestBody to indicate that the parameter should be obtained from the request body.
     * It is also annotated with @Valid to enable validation of the stock object based on
     * @return The method is returning an object of type Stock.View.
     */
    @PutMapping
    public Stock.View update(@RequestBody @Valid Stock.CreateView stock) {
        return stockService.save(stock);
    }

    /**
     * The function "getApplicableRegimen" returns a list of applicable regimens.
     *
     * @return The method is returning a List of Strings.
     */
    @GetMapping("/applicable-regimen")
    public List<String> getApplicableRegimen() {
        return stockService.applicableRegimen();
    }

    /**
     * The above function is a Java method that deletes a stock item by its ID.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that represents the unique identifier of the
     * resource to be deleted.
     */
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        stockService.deleteById(id);
    }
}
