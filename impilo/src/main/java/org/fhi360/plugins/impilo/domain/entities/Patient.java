package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.Mapping;
import com.blazebit.persistence.view.MappingSubquery;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.fhi360.plugins.impilo.domain.providers.DateOfNextRefillSubqueryProvider;
import org.fhi360.plugins.impilo.domain.providers.LastClinicDateSubqueryProvider;
import org.fhi360.plugins.impilo.domain.providers.LastRefillDateSubqueryProvider;
import org.fhi360.plugins.impilo.domain.providers.PatientSiteSubqueryProvider;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class Patient {
    @Id
    @UUIDV7
    private UUID id;

    private String givenName;

    private String familyName;

    private LocalDate dateOfBirth;

    private String sex;

    private String phoneNumber;

    private String address;

    private String hospitalNumber;

    private String uniqueId;

    private String regimen;

    private LocalDate lastViralLoadDate;

    private LocalDate nextClinicVisit;

    private LocalDate nextRefillDate;

    private LocalDate nextViralLoadDate;

    private LocalDate nextAppointmentDate;

    private LocalDate nextCervicalCancerDate;

    private LocalDate nextTptDate;

    private String personId;

    private String patientId;

    @ManyToOne
    private Organisation facility;

    @EntityView(Patient.class)
    public interface IdView {
        @IdMapping
        UUID getId();
    }

    @EntityView(Patient.class)
    public record ListView(
        @IdMapping UUID id, String givenName, String familyName, String sex,
        LocalDate dateOfBirth, String address,
        String phoneNumber, String hospitalNumber, String uniqueId, String regimen,
        @Mapping("facility.name")
        String facility,
        @MappingSubquery(PatientSiteSubqueryProvider.class)
        String site
    ) {
    }

    @EntityView(Patient.class)
    public record View(@IdMapping UUID id, String givenName, String familyName, String sex,
                       LocalDate dateOfBirth, String address, LocalDate nextAppointmentDate,
                       String phoneNumber, String hospitalNumber, String uniqueId, String regimen,
                       @MappingSubquery(LastRefillDateSubqueryProvider.class)
                       LocalDate lastRefillDate,
                       @MappingSubquery(LastClinicDateSubqueryProvider.class)
                       LocalDate lastClinicVisit,
                       @MappingSubquery(DateOfNextRefillSubqueryProvider.class)
                       LocalDate nextRefillDate, LocalDate nextCervicalCancerDate,
                       @Mapping("facility.name")
                       String facility, LocalDate nextTptDate, LocalDate nextViralLoadDate,
                       @Mapping("Refill[patient.id IN VIEW(id)]")
                       List<RefillView> refills) {
        @EntityView(Refill.class)
        public record RefillView(LocalDate date, LocalDate dateNextRefill, String regimen, Integer qtyDispensed,
                                 Integer qtyPrescribed) {

        }
    }
}
