package org.fhi360.plugins.impilo.config;

import org.fhi360.plugins.impilo.domain.ImpiloGatewayDomain;
import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import io.github.jbella.snl.core.api.domain.CoreDomain;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableEntityViews(basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class})
@EnableJpaRepositories(
    basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class}
)
@RequiredArgsConstructor
public class DomainConfiguration {
    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    @Bean(name = "transactionManager")
    @Primary
    public TransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setDataSource(dataSource);
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }
}
