package org.fhi360.plugins.impilo.integration;

import org.fhi360.plugins.impilo.ImpiloGatewayPluginApp;
import io.github.jbella.snl.core.api.config.ContextProvider;
import io.github.jbella.snl.test.core.TestConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.fhi360.plugins.impilo.services.RandomDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@SpringBootTest(classes = TestTutorialService.Config.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@DirtiesContext
@ContextConfiguration
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    MockitoTestExecutionListener.class
})
public class TestTutorialService {

    @Autowired
    private RandomDataService service;

    @Test
    public void should_create_service() throws Exception {
        Assertions.assertNotNull(service);
    }

    @ComponentScan(basePackageClasses = {
        ImpiloGatewayPluginApp.class, TestConfiguration.class, ContextProvider.class
    })
    static class Config {

    }
}
