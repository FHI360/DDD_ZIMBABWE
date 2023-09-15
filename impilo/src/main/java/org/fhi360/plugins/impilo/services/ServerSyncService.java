package org.fhi360.plugins.impilo.services;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.errors.SynchronisationError;
import org.springframework.stereotype.Service;

/**
 * The ServerSyncService class is responsible for synchronizing electronic health records (EHR) and central server data.
 */
@Service
@RequiredArgsConstructor
public class ServerSyncService {
    private final EHRService ehrService;
    private final FacilityServerService facilityServerService;


    /**
     * The syncEhr() function processes patients and saves transactions, throwing a SynchronisationError if an exception
     * occurs.
     */
    public void syncEhr() {
        try {
            ehrService.processPatients();
            ehrService.saveTransactions();
        } catch (Exception e) {
            throw new SynchronisationError(e.getMessage());
        }
    }

    /**
     * The function `synCentralServer()` synchronizes the central server with the facility server.
     */
    public void synCentralServer() {
        try {
            facilityServerService.synchronize();
        } catch (Exception e) {
            throw new SynchronisationError(e.getMessage());
        }
    }

    /**
     * The function schedules a task to be executed at regular intervals on a facility server.
     *
     * @param intervalSecs The intervalSecs parameter is the time interval in seconds at which the sync task should be
     * scheduled.
     */
    public void scheduleSync(long intervalSecs){
        facilityServerService.scheduleTask(intervalSecs);
    }

    public Long getCurrentAutoSyncInterval() {
        return facilityServerService.getCurrentAutoSyncInterval();
    }

}
