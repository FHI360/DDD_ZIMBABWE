package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.view.EntityViewManager;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.domain.Party;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.datafaker.Faker;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.services.models.Name;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Remove this class before deployment
 */
@Service
@RequiredArgsConstructor
public class RandomDataService {
    private final TransactionHandler transactionHandler;
    private final PatientRepository patientRepository;
    private final EntityViewManager evm;
    private final EntityManager em;
    Random rand = new Random();
    Faker faker = new Faker();

    //@PostConstruct
    public void init() {
        if (((long) em.createQuery("select count(o) from Organisation o where o.type = 'FACILITY'").getSingleResult()) == 0) {
            transactionHandler.runInTransaction(() -> {
                generateFacilities();
                generateSite();
                generatePatient();

                return null;
            });
        }
    }

    private void generateFacilities() {
        var org = evm.create(Organisation.CreateView.class);
        var party = evm.create(Party.PartyView.class);
        party.setType("FACILITY");
        org.setName(faker.medical().hospitalName());
        org.setParty(party);
        org.setType("FACILITY");

        evm.save(em, org);
    }

    private void generateSite() {
        for (int i = 0; i < 5; i++) {
            var org = evm.create(Organisation.CreateView.class);
            var party = evm.create(Party.PartyView.class);
            party.setType("OUTLET");
            org.setName(faker.medical().hospitalName());
            org.setParty(party);
            org.setType("OUTLET");
            evm.save(em, org);
        }
    }

    private void generatePatient() {
        CSVReader csvReader = null;
        try {
            URL url = this.getClass().getClassLoader().getResource("names.csv");
            assert url != null;
            csvReader = new CSVReader(new InputStreamReader(url.openStream()));
        } catch (IOException ignored) {
        }
        assert csvReader != null;
        CsvToBean<Name> cb = new CsvToBeanBuilder<Name>(csvReader)
                .withType(Name.class)
                .build();
        List<Name> names = cb.parse();

        List<Organisation> facilities = em.createQuery("select o from Organisation o where o.type = 'FACILITY'")
            .getResultList();
        Random rand = new Random();
        List<String> regimens = List.of("ABC(20mg/ml)+DDI(10mg/ml)+3TC(30mg)",
            "AZT(300mg)+3TC(150mg)+LPV/r(200/50mg)", "ABC(300mg)+3TC(150mg)+LPV/r(200/50mg)",
            "AZT/3TC(300/150mg)+EFV(200mg)", "3TC/FTC(300/300mg)+EFV(600mg)");
        for (int i = 0; i < 200; i++) {
            Patient patient = new Patient();
            Name name = names.get(i);
            patient.setGivenName(name.givenName);
            patient.setFamilyName(name.familyName);
            patient.setSex(faker.gender().binaryTypes().toLowerCase());
            patient.setDateOfBirth(faker.date().birthday().toLocalDateTime().toLocalDate());
            patient.setHospitalNumber(faker.idNumber().peselNumber());
            patient.setPhoneNumber(faker.phoneNumber().cellPhone());
            patient.setAddress(faker.address().fullAddress());
            patient.setFacilityId(facilities.get(0).getId().toString());
            patient.setPersonId(UUID.randomUUID().toString());
            patient.setFacilityName(facilities.get(0).getName());
            patient.setRegimen(regimens.get(rand.nextInt(regimens.size())));
            patient.setNextAppointmentDate(faker.date().future(180, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
            patient.setReference(UUID.randomUUID());
            patient.setOrganisation(facilities.get(0));

            patientRepository.save(patient);
        }
    }
}
