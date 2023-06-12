package org.fhi360.plugins.impilo.web.errors;

import io.github.jbella.snl.core.api.services.errors.ProblemDetailWithCause;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.net.URI;

import static io.github.jbella.snl.core.api.services.errors.ErrorConstants.PROBLEM_BASE_URL;

public class AuthenticationError extends ErrorResponseException {
    public AuthenticationError() {
        super(HttpStatus.UNAUTHORIZED, ProblemDetailWithCause.ProblemDetailWithCauseBuilder.instance()
            .withStatus(HttpStatus.UNAUTHORIZED.value())
            .withType(URI.create(PROBLEM_BASE_URL + "/authentication-error"))
            .withTitle("Authentication Error")
            .withDetail("Could not authenticate with the E.HR server")
            .build(), null);
    }
}
