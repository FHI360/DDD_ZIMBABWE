package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.view.EntityViewManager;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.domain.Party;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RandomDataService {
    private final TransactionHandler transactionHandler;
    private final PatientRepository patientRepository;
    private final RefillRepository refillRepository;
    private final EntityViewManager evm;
    private final EntityManager em;
    Random rand = new Random();
    Faker faker = new Faker();

    @PostConstruct
    public void init() {
        if (patientRepository.count() == 0) {
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
        org.setType("Facility");

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
        UUID facilityId = UUID.randomUUID();
        Random rand = new Random();
        List<String> regimens = List.of("ABC(20mg/ml)+DDI(10mg/ml)+3TC(30mg)",
            "AZT(300mg)+3TC(150mg)+LPV/r(200/50mg)", "ABC(300mg)+3TC(150mg)+LPV/r(200/50mg)",
            "AZT/3TC(300/150mg)+EFV(200mg)", "3TC/FTC(300/300mg)+EFV(600mg)");
        for (int i = 0; i < 200; i++) {
            Patient patient = new Patient();
            patient.setGivenName(faker.name().firstName());
            patient.setFamilyName(faker.name().lastName());
            patient.setSex(faker.gender().binaryTypes());
            patient.setDateOfBirth(faker.date().birthday().toLocalDateTime().toLocalDate());
            patient.setHospitalNumber(faker.idNumber().peselNumber());
            patient.setPhoneNumber(faker.phoneNumber().cellPhone());
            patient.setAddress(faker.address().fullAddress());
            patient.setFacilityId(facilityId.toString());
            patient.setFacilityName(faker.name().name());
            patient.setRegimen(regimens.get(rand.nextInt(regimens.size())));
            patient.setNextAppointmentDate(faker.date().future(180, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
            patient.setReference(UUID.randomUUID());

            patientRepository.save(patient);
        }
    }
}
