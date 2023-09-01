package org.fhi360.plugins.impilo.services;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.errors.SynchronisationError;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServerSyncService {
    private final EHRService ehrService;
    private final FacilityServerService facilityServerService;


    public void syncEhr() {
        try {
            ehrService.processPatients();
            ehrService.saveTransactions();
        } catch (Exception e) {
            throw new SynchronisationError(e.getMessage());
        }
    }

    public void synCentralServer() {
        try {
            facilityServerService.synchronize();
        } catch (Exception e) {
            throw new SynchronisationError(e.getMessage());
        }
    }
}
