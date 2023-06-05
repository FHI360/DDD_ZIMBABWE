package org.fhi360.plugins.impilo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.SiteActivationService;
import org.fhi360.plugins.impilo.web.models.ActivationData;
import org.fhi360.plugins.impilo.web.models.DiscontinuationData;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/activation")
@RequiredArgsConstructor
public class ActivationResource {
    private final SiteActivationService activationService;

    @GetMapping("/activate/site/{code}")
    public ActivationData activate(@PathVariable UUID code) {
        return activationService.activate(code);
    }

    @PostMapping("/discontinue-services/client/{uniqueId}/site/{code}")
    public boolean discontinueService(@PathVariable String uniqueId, @PathVariable String code,
                                      @RequestBody @Valid DiscontinuationData data) {
        return activationService.discontinueClient(code, uniqueId, data.getDate(), data.getReason());
    }
}
