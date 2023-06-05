package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.view.EntityViewManager;
import io.github.jbella.snl.core.api.domain.Identifier;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.domain.Party;
import io.github.jbella.snl.core.api.services.TransactionHandler;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.domain.repositories.SiteAssignmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RandomDataService {
    Random rand = new Random();
    Faker faker = new Faker();
    private final TransactionHandler transactionHandler;
    private final PatientRepository patientRepository;
    private final SiteAssignmentRepository siteAssignmentRepository;
    private final RefillRepository refillRepository;
    private final ClinicDataRepository clinicDataRepository;
    private final EntityViewManager evm;
    private final EntityManager em;

    @PostConstruct
    public void init() {
        if (patientRepository.count() == 0) {
            transactionHandler.runInTransaction(() -> {
                generateFacilities();
                generateSite();
                generatePatient();
                generateRefill();

                return null;
            });
        }
    }

    private void generateFacilities() {
        for (int i = 0; i < 2; i++) {
            var org = evm.create(Organisation.CreateView.class);
            var party = evm.create(Party.PartyView.class);
            party.setType("FACILITY");
            org.setName(faker.medical().hospitalName());
            org.setParty(party);
            org.setType("Facility");
            evm.save(em, org);
        }
    }

    private void generateSite() {
        for (int i = 0; i < 10; i++) {
            var org = evm.create(Organisation.CreateView.class);
            var party = evm.create(Party.PartyView.class);
            party.setType("OFCAD Site");
            var identifier = evm.create(Identifier.IdentifierView.class);
            identifier.setValue(faker.letterify("Z???????", true));
            identifier.setType("SITE_ID");
            identifier.setRegister("SYSTEM");
            party.setIdentifiers(Set.of(identifier));
            org.setName(faker.medical().hospitalName());
            org.setParty(party);
            org.setType("OFCAD Site");
            evm.save(em, org);
        }
    }

    private void generatePatient() {
        List<Organisation> facilities = em.createQuery("select o from Organisation o where o.type = 'Facility'")
            .getResultList();
        Random rand = new Random();
        List<String> regimens = List.of("ABC(20mg/ml)+DDI(10mg/ml)+3TC(30mg)",
            "AZT(300mg)+3TC(150mg)+LPV/r(200/50mg)", "ABC(300mg)+3TC(150mg)+LPV/r(200/50mg)",
            "AZT/3TC(300/150mg)+EFV(200mg)", "3TC/FTC(300/300mg)+EFV(600mg)");
        for (int i = 0; i < 3000; i++) {
            Patient patient = new Patient();
            patient.setGivenName(faker.name().firstName());
            patient.setFamilyName(faker.name().lastName());
            patient.setSex(faker.gender().binaryTypes());
            patient.setDateOfBirth(faker.date().birthday().toLocalDateTime().toLocalDate());
            patient.setHospitalNumber(faker.idNumber().peselNumber());
            patient.setUniqueId(faker.idNumber().invalidSvSeSsn());
            patient.setPhoneNumber(faker.phoneNumber().cellPhone());
            patient.setAddress(faker.address().fullAddress());
            patient.setRegimen(regimens.get(rand.nextInt(regimens.size())));
            patient.setLastViralLoadDate(faker.date().past(90, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
            patient.setFacility(facilities.get(rand.nextInt(facilities.size())));
            if (patient.getSex().equals("Female")) {
                patient.setNextCervicalCancerDate(faker.date().future(90, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
            }
            patient.setNextViralLoadDate(faker.date().future(180, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
            patient.setNextTptDate(faker.date().future(180, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
            patient.setNextAppointmentDate(faker.date().future(180, TimeUnit.DAYS).toLocalDateTime().toLocalDate());

            patientRepository.save(patient);
        }
    }

    private void generateClinicData() {
        patientRepository.findAll()
            .forEach(patient -> {
                for (int i = 0; i < rand.nextInt(1, 3); i++) {
                    ClinicData clinicData = new ClinicData();
                    clinicData.setPatient(patient);
                    clinicData.setDate(faker.date().past(90, TimeUnit.DAYS).toLocalDateTime().toLocalDate());
                    clinicData.setDiastolic(rand.nextInt(60, 120));
                    clinicData.setSystolic(rand.nextInt(60, 160));
                    clinicData.setWeight(rand.nextFloat(40, 180));
                    clinicData.setTemperature(rand.nextFloat(20, 43));
                    clinicData.setFever(rand.nextBoolean());
                    clinicData.setCoughing(rand.nextBoolean());
                    clinicData.setSweating(rand.nextBoolean());
                    clinicData.setSwelling(rand.nextBoolean());
                    clinicData.setTbReferred(rand.nextBoolean());
                        /*clinicData.setViralLoadDueDate(
                                faker.date().future(504, TimeUnit.DAYS,
                                                Date.from(clinicData.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()))
                                        .toLocalDateTime().toLocalDate());*/

                    clinicDataRepository.save(clinicData);
                }
            });
    }

    private void generateRefill() {
        List<Integer> doses = List.of(30, 60, 90, 120, 180);
        patientRepository.findAll()
            .forEach(patient -> {
                Refill previous = null;
                for (int i = 0; i < rand.nextInt(3, 6); i++) {
                    LocalDate date = faker.date().past(566, TimeUnit.DAYS).toLocalDateTime().toLocalDate();
                    int duration = doses.get(rand.nextInt(doses.size()));
                    if (previous != null) {
                        date = previous.getDateNextRefill();
                    }
                    Refill refill = new Refill();
                    refill.setDate(date);
                    refill.setPatient(patient);
                    refill.setRegimen(patient.getRegimen());
                    refill.setAdverseIssues(rand.nextBoolean());
                    refill.setMissedDose(rand.nextBoolean());
                    refill.setQtyPrescribed(duration);
                    refill.setQtyDispensed(refill.getQtyPrescribed());
                    refill.setDateNextRefill(refill.getDate().plusDays(refill.getQtyDispensed()));

                    if (!date.isAfter(LocalDate.now())) {
                        refillRepository.save(refill);
                    }

                    previous = refill;
                }
            });
    }
}
