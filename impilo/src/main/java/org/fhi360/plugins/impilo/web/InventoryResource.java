package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.fhi360.plugins.impilo.services.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * The InventoryResource class is a REST controller that handles requests related to stock issuance and interacts with the
 * InventoryService.
 */
@RestController
@RequestMapping("/api/impilo/stock-issuance")
@RequiredArgsConstructor
public class InventoryResource {
    private final InventoryService inventoryService;

    /**
     * The function returns a list of stock issuances for a given site code.
     *
     * @param code The "code" parameter in the above code snippet is a UUID (Universally Unique Identifier) that is used to
     * identify a specific site.
     * @return The method is returning a list of objects of type StockIssuance.View.
     */
    @GetMapping("/list/site/{code}")
    public List<StockIssuance.View> acknowledge(@PathVariable UUID code) {
        return inventoryService.list(code);
    }
}
