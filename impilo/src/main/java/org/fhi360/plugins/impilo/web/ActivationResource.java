package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.SiteActivationService;
import org.fhi360.plugins.impilo.web.models.ActivationData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/impilo/activation")
@RequiredArgsConstructor
public class ActivationResource {
    private final SiteActivationService activationService;

    @GetMapping("/activate")
    public ActivationData activate() {
        return activationService.activate();
    }
}
