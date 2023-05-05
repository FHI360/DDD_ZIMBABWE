package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.domain.Organisation;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.OfcadSiteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/impilo/ofcad-sites")
@RequiredArgsConstructor
public class OfcadSiteResource {
    private final OfcadSiteService siteService;

    @GetMapping
    public List<Organisation.ShortView> list(@RequestParam(required = false) String keyword) {
        return siteService.list(keyword);
    }
}
