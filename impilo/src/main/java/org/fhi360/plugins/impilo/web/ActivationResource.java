package org.fhi360.plugins.impilo.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.SiteActivationService;
import org.fhi360.plugins.impilo.services.models.ActivationData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The ActivationResource class is a REST controller that handles activation requests and uses the SiteActivationService to
 * activate a site.
 */
@RestController
@RequestMapping("/api/impilo/activation")
@RequiredArgsConstructor
public class ActivationResource {
    private final SiteActivationService activationService;

    /**
     * The above function is a GET request mapping that activates a service and returns activation data.
     *
     * @return The method is returning an object of type ActivationData.
     */
    @Operation(summary = "Pull data for a OFCAD site related to the current login user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "An object containing a list of patients devolved to the site and related site information",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ActivationData.class))})
    })
    @GetMapping("/activate")
    public ActivationData activate() {
        return activationService.activate();
    }
}
