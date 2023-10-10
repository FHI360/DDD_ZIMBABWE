package org.fhi360.plugins.impilo.integration;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.JoinType;
import com.blazebit.persistence.integration.view.spring.EnableEntityViews;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.glytching.junit.extension.random.Random;
import io.github.glytching.junit.extension.random.RandomBeansExtension;
import io.github.jbella.snl.core.api.config.ContextProvider;
import io.github.jbella.snl.core.api.domain.CoreDomain;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import io.github.jbella.snl.test.core.TestConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.fhi360.plugins.impilo.ImpiloGatewayPluginApp;
import org.fhi360.plugins.impilo.domain.ImpiloGatewayDomain;
import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Prescription;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.services.DevolveService;
import org.fhi360.plugins.impilo.services.RandomDataService;
import org.fhi360.plugins.impilo.services.SiteActivationService;
import org.fhi360.plugins.impilo.services.models.EHRSyncData;
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

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
@Slf4j
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
    @Autowired
    RefillRepository refillRepository;
    @Autowired
    DevolveService devolveService;
    @Autowired
    SiteActivationService siteActivationService;


    @Test
    public void should_create_service(@Random EHRSyncData data) throws Exception {
        refillRepository.findForSync(true);
        Faker faker = new Faker();
        String facilityId = UUID.randomUUID().toString();
        String facilityName = faker.medical().hospitalName();
        List<String> regimens = List.of("ABC(20mg/ml)+DDI(10mg/ml)+3TC(30mg)",
            "AZT(300mg)+3TC(150mg)+LPV/r(200/50mg)", "ABC(300mg)+3TC(150mg)+LPV/r(200/50mg)",
            "AZT/3TC(300/150mg)+EFV(200mg)", "3TC/FTC(300/300mg)+EFV(600mg)");
        var patients = data.getPatients().stream()
                .map(patient -> {
                    UUID patientId = UUID.randomUUID();
                    UUID personId = UUID.randomUUID();
                    patient.setAddress(faker.address().fullAddress());
                    patient.setGivenName(faker.name().firstName());
                    patient.setFamilyName(faker.name().lastName());
                    patient.setSex(faker.gender().binaryTypes().toLowerCase());
                    patient.setDateOfBirth(faker.date().birthday().toLocalDateTime().toLocalDate());
                    patient.setHospitalNumber(faker.idNumber().peselNumber());
                    patient.setPhoneNumber(faker.phoneNumber().cellPhone());
                    patient.setAddress(faker.address().fullAddress());
                    patient.setFacilityId(facilityId);
                    patient.setFacilityName(facilityName);
                    patient.setPersonId(personId.toString());
                    patient.setPatientId(patientId);
                    patient.setRegimen(regimens.get(new java.util.Random().nextInt(regimens.size())));
                    patient.setNextAppointmentDate(faker.date().future(180, TimeUnit.DAYS).toLocalDateTime().toLocalDate());

                    UUID medicineId = UUID.randomUUID();
                    var prescriptions = patient.getPrescriptions().stream()
                        .map(prescription -> {
                            prescription.setPrescriptionId(UUID.randomUUID().toString());
                            prescription.setMedicineId(medicineId.toString());
                            prescription.setFrequencyId(UUID.randomUUID().toString());
                            prescription.setMedicineName(patient.getRegimen());
                            prescription.setTime(faker.date().past(180, TimeUnit.DAYS).toLocalDateTime());
                            prescription.setPrescribedQty(new java.util.Random().nextInt(30, 180));
                            return prescription;
                        }).toList();
                    patient.setPrescriptions(prescriptions);
                    return patient;
                }).toList();
        data.setPatients(patients);

        var stocks = data.getStocks().stream()
            .map(stock -> {
                stock.setBatchIssuanceId(UUID.randomUUID().toString());
                stock.setBottles(new java.util.Random().nextLong(500, 2000));
                stock.setDate(faker.date().past(90, TimeUnit.DAYS).toLocalDateTime());
                stock.setBatchNo(Long.toString(faker.barcode().ean13()));
                stock.setSerialNo(faker.number().digits(9));
                stock.setExpirationDate(faker.date().future(360, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
                stock.setManufactureDate(faker.date().past(180, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
                stock.setRegimen(regimens.get(new java.util.Random().nextInt(regimens.size())));

                return stock;
            }).toList();
        data.setStocks(stocks);
LOG.info("Data: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));

        var settings5 = EntityViewSetting.create(StockIssuance.CreateView.class);
        var cb5 = cbf.create(em, StockIssuance.class)
            .where("site.id").in(List.of(UUID.randomUUID()))
            .where("synced").eq(false);
        List<StockIssuance.CreateView> issuance = evm.applySetting(settings5, cb5).getResultList();

    }

    @Test
    @Transactional
    public void testPatient(@Random Patient patient) {
        var settings2 = EntityViewSetting.create(Patient.CreateView.class);
        //@formatter:off
        var settings1 = EntityViewSetting.create(Devolve.CreateView.class);
        var cb1 = cbf.create(em, Devolve.class)
            .where("patient.organisation.id").eq(UUID.fromString("018a5c7c-6b7c-7fd0-a43c-66c257938552"))
            .where("synced").eq(false);
        List<Devolve.CreateView> devolves = evm.applySetting(settings1, cb1).getResultList();
    }

    @ComponentScan(basePackageClasses = {
        ImpiloGatewayPluginApp.class, TestConfiguration.class, ContextProvider.class
    })
    @Import(TransactionHandler.class)
    @EnableEntityViews(basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class})
    @EntityScan(basePackageClasses = {CoreDomain.class, ImpiloGatewayDomain.class})
    static class Config {

    }
}
