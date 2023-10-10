package org.fhi360.plugins.impilo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.EHRSyncService;
import org.fhi360.plugins.impilo.services.models.EHRData;
import org.fhi360.plugins.impilo.services.models.EHRSyncData;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/ehr-sync")
@RequiredArgsConstructor
public class EHRResource {
    private final EHRSyncService syncService;

    @PostMapping
    public boolean saveData(@RequestBody @Validated @Valid EHRSyncData syncData) {
        return syncService.syncData(syncData);
    }

    @GetMapping
    public EHRData getData(@RequestParam(required = false, defaultValue = "false") boolean all) {
        return syncService.getData(all);
    }

    @GetMapping("/acknowledge/{reference}")
    public void acknowledge(@PathVariable UUID reference) {
        syncService.acknowledge(reference);
    }
}
