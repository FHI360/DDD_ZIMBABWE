package org.fhi360.plugins.impilo.services.errors;

import io.github.jbella.snl.core.api.services.errors.ProblemDetailWithCause;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

import static io.github.jbella.snl.core.api.services.errors.ErrorConstants.PROBLEM_BASE_URL;

public class SynchronisationError  extends ErrorResponseException {
    public SynchronisationError(String message) {
        super(HttpStatus.BAD_REQUEST, ProblemDetailWithCause.ProblemDetailWithCauseBuilder.instance()
            .withStatus(HttpStatus.BAD_REQUEST.value())
            .withType(URI.create(PROBLEM_BASE_URL + "/bad-request"))
            .withTitle("Bad Request")
            .withDetail(message)
            .build(), null);
    }
}
