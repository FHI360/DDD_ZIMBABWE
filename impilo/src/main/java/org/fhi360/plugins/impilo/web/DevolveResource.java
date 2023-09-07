package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.services.DevolveService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/devolves")
@RequiredArgsConstructor
public class DevolveResource {
    private final DevolveService devolveService;

    @GetMapping
    public PagedResult<Patient.ListView> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Boolean assigned,
                                              @RequestParam(required = false, defaultValue = "0") int start,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return devolveService.list(keyword, assigned, start, pageSize);
    }

    @GetMapping("devolve/patient/{patientId}/organisation/{organisationId}")
    public Boolean assign(@PathVariable UUID organisationId, @PathVariable UUID patientId) {
        return devolveService.devolve(organisationId, patientId);
    }

    @GetMapping("unassign/patient/{patientId}")
    public Boolean unassign(@PathVariable UUID patientId) {
        return devolveService.unassign(patientId);
    }
}
