package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.CentralServerService;
import org.fhi360.plugins.impilo.services.model.FacilityData;
import org.fhi360.plugins.impilo.services.model.ServerData;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/server-sync")
@RequiredArgsConstructor
public class CentralServerResource {
    private final CentralServerService serverService;

    @GetMapping("/facility-data/{facilityId}")
    public FacilityData getFacilityData(@PathVariable UUID facilityId,
                                        @RequestParam(required = false) List<UUID> outletIds) {
        return serverService.retrieveFacilityData(facilityId, outletIds);
    }

    @PostMapping("/server-data")
    public boolean saveData(@RequestBody ServerData data) {
        return serverService.saveData(data);
    }

    @GetMapping("/acknowledge/{reference}")
    public void acknowledge(@PathVariable UUID reference) {
        serverService.acknowledge(reference);
    }
}
