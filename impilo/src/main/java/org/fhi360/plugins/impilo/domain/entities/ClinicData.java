package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.CreatableEntityView;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.UpdatableEntityView;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The ClinicData class represents data collected during a clinic visit, including patient information and various health
 * measurements.
 */
@Entity
@Getter
@Setter
@Table(name = "imp_clinic_data")
public class ClinicData {
    @NotNull
    LocalDate date;
    @Id
    @UUIDV7
    private UUID id;
    private Float weight;

    private Integer diastolic;

    private Integer systolic;

    private Float temperature;

    private Boolean coughing;

    private Boolean sweating;

    private Boolean weightLoss;

    private Boolean swelling;

    private Boolean fever;

    private Boolean tbReferred;

    private Boolean synced = false;

    private UUID reference;

    @ManyToOne
    @NotNull
    private Patient patient;

    @ManyToOne
    @NotNull
    private Organisation organisation;

    @EntityView(ClinicData.class)
    @CreatableEntityView
    @Schema(name = "ClinicDataCreateView")
    public interface CreateView {
        @IdMapping
        UUID getId();

        @NotNull
        LocalDate getDate();

        void setDate(LocalDate date);

        Float getWeight();

        void setWeight(Float weight);

        Integer getDiastolic();

        void setDiastolic(Integer diastolic);

        Integer getSystolic();

        void setSystolic(Integer systolic);

        Float getTemperature();

        void setTemperature(Float temperature);

        Boolean getCoughing();

        void setCoughing(Boolean coughing);

        Boolean getSweating();

        void setSweating(Boolean sweating);

        Boolean getWeightLoss();

        void setWeightLoss(Boolean weightLoss);

        Boolean getSwelling();

        void setSwelling(Boolean swelling);

        Boolean getFever();

        void setFever(Boolean fever);

        Boolean getTbReferred();

        void setTbReferred(Boolean tbReferred);

        Boolean getSynced();

        void setSynced(Boolean synced);

        UUID getReference();

        void setReference(UUID reference);

        Patient.IdView getPatient();

        void setPatient(Patient.IdView patient);

        Organisation.IdView getOrganisation();

        void setOrganisation(Organisation.IdView organisation);
    }

    @EntityView(ClinicData.class)
    @UpdatableEntityView
    public interface UpdateView extends CreateView {
        void setId(UUID id);
    }
}
