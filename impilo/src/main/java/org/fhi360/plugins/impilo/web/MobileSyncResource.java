package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.MobileSyncService;
import org.fhi360.plugins.impilo.services.model.SyncData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/impilo/mobile-sync")
@RequiredArgsConstructor
public class MobileSyncResource {
    private final MobileSyncService mobileSyncService;

    @PostMapping
    public Boolean sync(@RequestBody SyncData data) {
        return mobileSyncService.sync(data);
    }
}
