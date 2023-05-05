package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.CreatableEntityView;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.UpdatableEntityView;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ClinicData {
    @Id
    @UUIDV7
    private UUID id;

    @NotNull
    LocalDate date;

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

    private LocalDate viralLoadDueDate;

    @ManyToOne
    private Patient patient;

    @EntityView(ClinicData.class)
    @CreatableEntityView
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

        LocalDate getViralLoadDueDate();

        void setViralLoadDueDate(LocalDate date);

        Patient.IdView getPatient();
        void setPatient(Patient.IdView patient);
    }

    @EntityView(ClinicData.class)
    @UpdatableEntityView
    public interface UpdateView extends CreateView {
        void setId(UUID id);
    }
}
