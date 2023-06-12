package org.fhi360.plugins.impilo.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.EHRService;
import org.fhi360.plugins.impilo.web.models.SyncDetail;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/impilo/sync")
@RequiredArgsConstructor
public class SyncResource {
    private final EHRService ehrService;

    @PostMapping
    public boolean sync(@Valid @RequestBody SyncDetail detail) throws URISyntaxException, IOException, InterruptedException {
        return ehrService.sync(detail.getUsername(), detail.getPassword());
    }
}
