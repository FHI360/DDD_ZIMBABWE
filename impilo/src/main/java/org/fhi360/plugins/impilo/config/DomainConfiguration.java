package org.fhi360.plugins.impilo.config;

import io.github.jbella.snl.core.api.domain.CoreDomain;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.ImpiloGatewayDomain;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
    basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class}
)
@RequiredArgsConstructor
//@Import(TransactionHandler.class)
public class DomainConfiguration {

}
