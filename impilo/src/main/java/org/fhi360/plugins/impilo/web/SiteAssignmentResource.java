package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.services.SiteAssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/site-assignments")
@RequiredArgsConstructor
public class SiteAssignmentResource {
    private final SiteAssignmentService siteAssignmentService;

    @GetMapping
    public PagedResult<Patient.ListView> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Boolean assigned,
                                              @RequestParam(required = false, defaultValue = "0") int start,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return siteAssignmentService.list(keyword, assigned, start, pageSize);
    }

    @GetMapping("assign/patient/{patientId}/site/{siteId}")
    public Boolean assign(@PathVariable UUID siteId, @PathVariable UUID patientId) {
        return siteAssignmentService.assign(siteId, patientId);
    }

    @GetMapping("unassign/patient/{patientId}")
    public Boolean unassign(@PathVariable UUID patientId) {
        return siteAssignmentService.unassign(patientId);
    }
}
