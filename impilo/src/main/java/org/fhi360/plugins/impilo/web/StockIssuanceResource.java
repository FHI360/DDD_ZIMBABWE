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

@RestController
@RequestMapping("/api/impilo/stock-issuance")
@RequiredArgsConstructor
public class StockIssuanceResource {
    private final StockIssuanceService issuanceService;

    @GetMapping("/{id}")
    public StockIssuance.View getById(@PathVariable UUID id) {
        return issuanceService.getById(id);
    }

    @GetMapping
    public PagedResult<StockIssuance.View> list(@RequestParam(required = false) String keyword,
                                                @RequestParam(required = false, defaultValue = "0") int start,
                                                @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return issuanceService.list(keyword, start, pageSize);
    }

    @GetMapping("/available-stock")
    public List<Stock.View> availableStock() {
        return issuanceService.listAvailableStock();
    }

    @PostMapping
    public StockIssuance.View save(@RequestBody @Valid StockIssuance.CreateView stock) {
        return issuanceService.save(stock);
    }

    @PutMapping
    public StockIssuance.View update(@RequestBody @Valid StockIssuance.CreateView stock) {
        return issuanceService.save(stock);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        issuanceService.deleteById(id);
    }
}
