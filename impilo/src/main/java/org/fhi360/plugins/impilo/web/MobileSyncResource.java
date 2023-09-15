package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.MobileSyncService;
import org.fhi360.plugins.impilo.services.models.SyncData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The MobileSyncResource class is a REST controller that handles requests to sync mobile data using the MobileSyncService.
 */
@RestController
@RequestMapping("/api/impilo/mobile-sync")
@RequiredArgsConstructor
public class MobileSyncResource {
    private final MobileSyncService mobileSyncService;

    /**
     * The above function is a POST endpoint that receives a SyncData object as a request body and returns a Boolean
     * indicating whether the data was successfully synced.
     *
     * @param data The "data" parameter is of type SyncData and is annotated with @RequestBody. This means that it will be
     * populated with the JSON data sent in the request body of the HTTP POST request.
     * @return A Boolean value is being returned.
     */
    @PostMapping
    public Boolean sync(@RequestBody SyncData data) {
        return mobileSyncService.sync(data);
    }
}
