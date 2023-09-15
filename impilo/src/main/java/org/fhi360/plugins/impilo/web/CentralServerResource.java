package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.CentralServerService;
import org.fhi360.plugins.impilo.services.models.FacilityData;
import org.fhi360.plugins.impilo.services.models.ServerData;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * The CentralServerResource class is a REST controller that handles requests related to syncing data between a central
 * server and client facilities.
 */
@RestController
@RequestMapping("/api/impilo/server-sync")
@RequiredArgsConstructor
public class CentralServerResource {
    private final CentralServerService serverService;

    /**
     * The function `getFacilityData` retrieves facility data for a given facility ID and optional list of outlet IDs.
     *
     * @param facilityId The facilityId parameter is a UUID (Universally Unique Identifier) that represents the unique
     * identifier of a facility.
     * @param outletIds The `outletIds` parameter is an optional query parameter that accepts a list of UUIDs. It is not
     * required to be provided in the request, hence the `required = false` annotation. If provided, it is used as a filter
     * to retrieve specific outlet data related to the facility identified by
     * @return The method is returning an object of type FacilityData.
     */
    @GetMapping("/facility-data/{facilityId}")
    public FacilityData getFacilityData(@PathVariable UUID facilityId,
                                        @RequestParam(required = false) List<UUID> outletIds) {
        return serverService.retrieveFacilityData(facilityId, outletIds);
    }

    /**
     * The function saves server data received in the request body and returns a boolean indicating whether the data was
     * successfully saved.
     *
     * @param data The "data" parameter is of type ServerData and is annotated with @RequestBody. This means that the data
     * will be received from the request body of the HTTP POST request and will be automatically converted from JSON to a
     * ServerData object.
     * @return The method is returning a boolean value.
     */
    @PostMapping("/server-data")
    public boolean saveData(@RequestBody ServerData data) {
        return serverService.saveData(data);
    }

    /**
     * The `acknowledge` function in Java is a GET request mapping that takes a UUID reference as a path variable and calls
     * the `acknowledge` method of the `serverService` object.
     *
     * @param reference The "reference" parameter is a UUID (Universally Unique Identifier) that is passed as a path
     * variable in the URL. It is used to identify a specific resource or entity that needs to be acknowledged.
     */
    @GetMapping("/acknowledge/{reference}")
    public void acknowledge(@PathVariable UUID reference) {
        serverService.acknowledge(reference);
    }
}
