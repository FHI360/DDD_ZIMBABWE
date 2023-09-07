package org.fhi360.plugins.impilo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.ServerSyncService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/impilo/server-sync")
@RequiredArgsConstructor
public class ServerSyncResource {
    private final ServerSyncService serverSyncService;

    @GetMapping("/ehr")
    public void syncEhr() {
        serverSyncService.syncEhr();
    }

    @GetMapping("/central-server")
    public void syncCentralServer(){
        serverSyncService.synCentralServer();
    }

    @PostMapping("/schedule-sync")
    public void scheduleSync(@RequestBody @Valid long intervalSecs) {
        serverSyncService.scheduleSync(intervalSecs);
    }
}
