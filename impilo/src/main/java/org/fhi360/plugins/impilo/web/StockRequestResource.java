package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.StockRequest;
import org.fhi360.plugins.impilo.services.StockRequestService;
import org.fhi360.plugins.impilo.services.models.ARVRequest;
import org.fhi360.plugins.impilo.services.models.StockFulfill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The StockRequestResource class is a REST controller that handles requests related to stock requests and fulfillment in
 * an inventory management system.
 */
@RestController
@RequestMapping("/api/impilo/inventory")
@RequiredArgsConstructor
public class StockRequestResource {
    private final StockRequestService requestService;

    /**
     * The function retrieves a stock request by its ID and returns it as a ResponseEntity.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that is used to identify a specific stock
     * request.
     * @return The method is returning a ResponseEntity object that wraps the result of the requestService.getById(id)
     * method call.
     */
    @GetMapping("/request/{id}")
    public ResponseEntity<StockRequest.CreateView> getById(@PathVariable UUID id) {
        return ResponseEntity.of(requestService.getById(id));
    }

    /**
     * The function `requestStock` saves a request with the given parameters.
     *
     * @param siteId The siteId parameter is a UUID (Universally Unique Identifier) that represents the unique identifier
     * for a site.
     * @param uniqueId The `uniqueId` parameter is a string that represents a unique identifier for the request.
     * @param date The "date" parameter is of type LocalDate and represents the date for which the stock is being
     * requested.
     * @param request The "request" parameter is of type ARVRequest and is annotated with @RequestBody. This means that it
     * will be deserialized from the request body of the HTTP POST request. The @Valid annotation indicates that the
     * request object should be validated according to any validation constraints specified on its fields.
     * @return The method is returning a boolean value.
     */
    @PostMapping("/request/site/{siteId}/date/{date}/unique-id/{uniqueId}")
    public boolean requestStock(@PathVariable UUID siteId, @PathVariable UUID uniqueId,
                                @PathVariable LocalDate date, @Valid @RequestBody ARVRequest request) {
        return requestService.saveRequest(date, siteId, uniqueId, request);
    }

    /**
     * The function returns a paged result of stock requests based on optional parameters such as site ID, ARV drug, start
     * index, and page size.
     *
     * @param siteId The siteId parameter is a UUID (Universally Unique Identifier) that represents the ID of a site. It is
     * an optional parameter, meaning it is not required for the request to be made.
     * @param arvDrug The "arvDrug" parameter is a string parameter that is used to filter the stock requests based on the
     * ARV drug name. It is an optional parameter, meaning it is not required to be provided in the request.
     * @param start The "start" parameter is used to specify the starting index of the list of stock requests to be
     * returned. It is an optional parameter and has a default value of 0 if not provided.
     * @param pageSite The "pageSite" parameter is used to specify the number of items to be displayed per page in the
     * list. It has a default value of 10, but you can override it by providing a different value in the request.
     * @return The method is returning a PagedResult object of type StockRequest.CreateView.
     */
    @GetMapping("/request")
    public PagedResult<StockRequest.ListView> list(@RequestParam(required = false) UUID siteId,
                                                     @RequestParam(required = false) String arvDrug,
                                                     @RequestParam(required = false, defaultValue = "0") int start,
                                                     @RequestParam(required = false, defaultValue = "10") int pageSite) {
        return requestService.list(siteId, arvDrug, start, pageSite);
    }

    /**
     * The fulfill function retrieves a list of StockFulfill objects based on the given siteId.
     *
     * @param siteId The siteId parameter is of type UUID and represents the unique identifier of a site. It is used to
     * identify a specific site for stock fulfillment.
     * @param uniqueId The uniqueId parameter is a String that represents a unique identifier for a specific item or
     * resource.
     * @return The method is returning a List of StockFulfill objects.
     */
    @GetMapping("/fulfillment/site/{siteId}/unique-id/{uniqueId}")
    public List<StockFulfill> fulfill(@PathVariable UUID siteId, @PathVariable String uniqueId) {
        return requestService.fulfill(siteId);
    }
}
