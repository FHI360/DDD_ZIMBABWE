package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.domain.Organisation;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.OutletService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The OutletResource class is a REST controller that handles requests related to OFCAD sites and provides a list of short
 * views of organisations.
 */
@RestController
@RequestMapping("/api/impilo/ofcad-sites")
@RequiredArgsConstructor
public class OutletResource {
    private final OutletService siteService;

    /**
     * The above function is a GET request handler that returns a list of short views of organizations based on an optional
     * keyword parameter.
     *
     * @param keyword The "keyword" parameter is an optional parameter that can be passed to the "list" method. It is of
     * type String and is annotated with @RequestParam(required = false), which means that it is not required to be
     * provided when calling the method. If a value is provided for the "keyword"
     * @return The method is returning a List of Organisation.ShortView objects.
     */
    @GetMapping
    public List<Organisation.ShortView> list(@RequestParam(required = false) String keyword) {
        return siteService.list(keyword);
    }
}
