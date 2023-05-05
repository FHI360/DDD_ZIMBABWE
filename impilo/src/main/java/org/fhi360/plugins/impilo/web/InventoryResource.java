package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.fhi360.plugins.impilo.services.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/impilo/stock-issuance")
@RequiredArgsConstructor
public class InventoryResource {
    private final InventoryService inventoryService;

    @GetMapping("/list/site/{code}")
    public List<StockIssuance.View> acknowledge(@PathVariable String code) {
        return inventoryService.list(code);
    }
}
