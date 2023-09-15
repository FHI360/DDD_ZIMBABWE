package org.fhi360.plugins.impilo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.ServerSyncService;
import org.springframework.web.bind.annotation.*;

/**
 * The ServerSyncResource class is a REST controller that handles requests related to syncing data between the server and
 * external systems.
 */
@RestController
@RequestMapping("/api/impilo/server-sync")
@RequiredArgsConstructor
public class ServerSyncResource {
    private final ServerSyncService serverSyncService;

    /**
     * The function syncEhr() is a GET request mapping that calls the syncEhr() method from the serverSyncService.
     */
    @GetMapping("/ehr")
    public void syncEhr() {
        serverSyncService.syncEhr();
    }

    /**
     * The function syncCentralServer() is a GET request mapping that calls the synCentralServer() method from the
     * serverSyncService.
     */
    @GetMapping("/central-server")
    public void syncCentralServer(){
        serverSyncService.synCentralServer();
    }

    /**
     * The function "scheduleSync" is a POST request handler that schedules a synchronization task with a given interval in
     * seconds.
     *
     * @param intervalSecs The parameter "intervalSecs" is of type long and represents the interval in seconds at which the
     * synchronization should be scheduled.
     */
    @PostMapping("/schedule-sync")
    public void scheduleSync(@RequestBody @Valid long intervalSecs) {
        serverSyncService.scheduleSync(intervalSecs);
    }

    @GetMapping("/sync-interval")
    public Long syncInterval() {
        return serverSyncService.getCurrentAutoSyncInterval();
    }
}
