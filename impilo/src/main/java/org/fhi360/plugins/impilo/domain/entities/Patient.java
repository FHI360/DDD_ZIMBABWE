package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.*;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.fhi360.plugins.impilo.domain.providers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
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

    private String regimen;

    private LocalDate nextAppointmentDate;

    private String personId;

    private String patientId;

    private String facilityId;

    private String facilityName;

    private UUID reference;

    @ManyToOne
    @NotNull
    private Organisation organisation;

    @EntityView(Patient.class)
    public interface IdView {
        @IdMapping
        UUID getId();
    }

    @EntityView(Patient.class)
    @CreatableEntityView
    public interface CreateView extends IdView {
        String getFamilyName();

        void setFamilyName(String name);

        String getGivenName();

        void setGivenName(String name);

        LocalDate getDateOfBirth();

        void setDateOfBirth(LocalDate dateOfBirth);

        String getSex();

        void setSex(String sex);

        String getPhoneNumber();

        void setPhoneNumber(String phoneNumber);

        String getAddress();

        void setAddress(String address);

        String getHospitalNumber();

        void setHospitalNumber(String hospitalNumber);

        String getRegimen();

        void setRegimen(String regimen);

        LocalDate getNextAppointmentDate();

        void setNextAppointmentDate(LocalDate appointmentDate);

        String getPatientId();

        void setPatientId(String patientId);

        String getPersonId();

        void setPersonId(String personId);

        String getFacilityId();

        void setFacilityId(String facilityId);

        String getFacilityName();

        void setFacilityName(String facilityName);

        UUID getReference();

        void setReference(UUID reference);

        Organisation.IdView getOrganisation();

        void setOrganisation(Organisation.IdView organisation);
    }

    @EntityView(Patient.class)
    @UpdatableEntityView
    public interface UpdateView extends CreateView {
        void setId(UUID id);
    }

    @EntityView(Patient.class)
    public record ListView(
        @IdMapping UUID id, String givenName, String familyName, String sex,
        LocalDate dateOfBirth, String address,
        String phoneNumber, String hospitalNumber, String regimen,
        String facilityName,
        @MappingSubquery(PatientSiteNameSubqueryProvider.class)
        String site
    ) {
    }

    @EntityView(Patient.class)
    public record View(@IdMapping UUID id, String givenName, String familyName, String sex,
                       LocalDate dateOfBirth, String address, LocalDate nextAppointmentDate,
                       String phoneNumber, String hospitalNumber, @Mapping("regimen") String assignedRegimen,
                       @MappingSubquery(LastRefillDateSubqueryProvider.class)
                       LocalDate lastRefillDate,
                       @MappingSubquery(LastClinicDateSubqueryProvider.class)
                       LocalDate lastClinicVisit,
                       @MappingSubquery(DateOfNextRefillSubqueryProvider.class)
                       LocalDate nextRefillDate,
                       @MappingSubquery(PatientSiteCodeSubqueryProvider.class)
                       UUID siteCode,
                       @MappingSubquery(PatientSiteNameSubqueryProvider.class)
                       String facility) {
    }
}
