package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.ServerSyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/impilo/sync")
@RequiredArgsConstructor
public class ServerSyncResource {
    private final ServerSyncService serverSyncService;

    @GetMapping("/ehr")
    public void syncEhr() throws Exception {
        serverSyncService.syncEhr();
    }

    @GetMapping("/central-server")
    public void syncCentralServer() throws Exception {
        serverSyncService.synCentralServer();
    }
}
