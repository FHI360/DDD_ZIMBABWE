package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.EHRSyncService;
import org.fhi360.plugins.impilo.services.models.EHRData;
import org.fhi360.plugins.impilo.services.models.EHRSyncData;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/ehr-sync")
@RequiredArgsConstructor
public class EHRResource {
    private final EHRSyncService syncService;

    @PostMapping
    public void saveData(@RequestBody EHRSyncData syncData) {
        syncService.syncData(syncData);
    }

    @GetMapping
    public EHRData getData() {
        return syncService.getData();
    }

    @GetMapping("/acknowledge/{reference}")
    public void acknowledge(@PathVariable UUID reference) {
        syncService.acknowledge(reference);
    }
}
