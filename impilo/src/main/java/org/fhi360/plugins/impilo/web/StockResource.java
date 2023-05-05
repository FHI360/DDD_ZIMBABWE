package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Stock;
import org.fhi360.plugins.impilo.services.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/stocks")
@RequiredArgsConstructor
public class StockResource {
    private final StockService stockService;

    @GetMapping("/{id}")
    public Stock.View getById(@PathVariable UUID id) {
        return stockService.getById(id);
    }

    @GetMapping
    public PagedResult<Stock.View> list(@RequestParam(required = false) String keyword,
                                        @RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return stockService.list(keyword, start, pageSize);
    }

    @PostMapping
    public Stock.View save(@RequestBody @Valid Stock.CreateView stock) {
        return stockService.save(stock);
    }

    @PutMapping
    public Stock.View update(@RequestBody @Valid Stock.CreateView stock) {
        return stockService.save(stock);
    }

    @GetMapping("/applicable-regimen")
    public List<String> getApplicableRegimen() {
        return stockService.applicableRegimen();
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable UUID id) {
        stockService.deleteById(id);
    }
}
