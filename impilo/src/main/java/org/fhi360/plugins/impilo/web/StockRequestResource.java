package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.StockRequest;
import org.fhi360.plugins.impilo.services.StockRequestService;
import org.fhi360.plugins.impilo.web.models.ARVRequest;
import org.fhi360.plugins.impilo.web.models.StockFulfill;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/inventory")
@RequiredArgsConstructor
public class StockRequestResource {
    private final StockRequestService requestService;

    @GetMapping("/request/{id}")
    public ResponseEntity<StockRequest.CreateView> getById(@PathVariable UUID id) {
        return ResponseEntity.of(requestService.getById(id));
    }

    @PostMapping("/request/site/{siteId}/date/{date}/unique-id/{uniqueId}")
    public boolean requestStock(@PathVariable UUID siteId, @PathVariable String uniqueId,
                                @PathVariable LocalDate date, @Valid @RequestBody ARVRequest request) {
        return requestService.saveRequest(date, siteId, uniqueId, request);
    }

    @GetMapping("/request")
    public PagedResult<StockRequest.CreateView> list(@RequestParam(required = false) UUID siteId,
                                                     @RequestParam(required = false) String arvDrug,
                                                     @RequestParam(required = false, defaultValue = "0") int start,
                                                     @RequestParam(required = false, defaultValue = "10") int pageSite) {
        return requestService.list(siteId, arvDrug, start, pageSite);
    }

    @GetMapping("/fulfillment/site/{siteId}/unique-id/{uniqueId}")
    public List<StockFulfill> fulfill(@PathVariable UUID siteId, @PathVariable String uniqueId) {
        return requestService.fulfill(siteId);
    }
}
