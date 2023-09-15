package org.fhi360.plugins.impilo.config;

import io.github.jbella.snl.core.api.domain.CoreDomain;
import org.fhi360.plugins.impilo.domain.ImpiloGatewayDomain;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The DomainConfiguration class is a Java configuration class that enables JPA repositories for the CoreDomain and
 * ImpiloGatewayDomain classes.
 */
@Configuration
@EnableJpaRepositories(
    basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class}
)
public class DomainConfiguration {

}
