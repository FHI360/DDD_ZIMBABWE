package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.Criteria;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.spi.CriteriaBuilderConfiguration;
import com.blazebit.persistence.view.ConfigurationProperties;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.spi.EntityViewConfiguration;
import io.github.jbella.snl.core.api.config.AuditViewListenersConfiguration;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.UUID;

@Configuration

public class BlazePersistenceConfiguration {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    public CriteriaBuilderFactory createCriteriaBuilderFactory() {
        CriteriaBuilderConfiguration config = Criteria.getDefault();
        return config.createCriteriaBuilderFactory(entityManagerFactory);
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Lazy(false)
    public EntityViewManager entityViewManager(CriteriaBuilderFactory cbf, EntityViewConfiguration entityViewConfiguration) {
        entityViewConfiguration.setProperty(ConfigurationProperties.UPDATER_STRICT_CASCADING_CHECK, "false");
        entityViewConfiguration.setProperty(ConfigurationProperties.UPDATER_FLUSH_MODE, "partial");
        entityViewConfiguration.setTypeTestValue(UUID.class, UUID.randomUUID());
        entityViewConfiguration.addEntityViewListener(AuditViewListenersConfiguration.class);
        return entityViewConfiguration.createEntityViewManager(cbf);
    }
 }
