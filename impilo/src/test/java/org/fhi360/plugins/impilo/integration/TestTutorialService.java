package org.fhi360.plugins.impilo.integration;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.view.EntityViewManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import io.github.jbella.snl.core.api.domain.CoreDomain;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import jakarta.persistence.EntityManager;
import org.fhi360.plugins.impilo.ImpiloGatewayPluginApp;
import io.github.jbella.snl.core.api.config.ContextProvider;
import io.github.jbella.snl.test.core.TestConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.fhi360.plugins.impilo.domain.ImpiloGatewayDomain;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.services.RandomDataService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TestTutorialService.Config.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class, RandomBeansExtension.class})
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
    @Autowired
    EntityManager em;
    @Autowired
    EntityViewManager evm;
    @Autowired
    CriteriaBuilderFactory cbf;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TransactionHandler transactionHandler;
    @Autowired
    PatientRepository patientRepository;

    @Test
    public void should_create_service() throws Exception {
        assertNotNull(service);
    }

    @Test
    @Transactional
    public void testPatient(@Random Patient patient) {
        var id = patient.getId();
        //var p = objectMapper.convertValue(patient, Patient.CreateView.class);
        var p = transactionHandler.runInTransaction(() -> {
           // evm.save(em, p);
            return patientRepository.save(patient);
        });
        assertEquals(id, p.getId());
    }

    @ComponentScan(basePackageClasses = {
        ImpiloGatewayPluginApp.class, TestConfiguration.class, ContextProvider.class
    })
    @EnableEntityViews(basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class})
    @EntityScan(basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class})
    static class Config {

    }
}
