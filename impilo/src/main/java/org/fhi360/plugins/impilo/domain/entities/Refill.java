package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.*;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString(exclude = "patient")
public class Refill {
    @Id
    @UUIDV7
    private UUID id;

    private LocalDate date;

    private LocalDate dateNextRefill;

    private String regimen;

    private Integer qtyPrescribed;

    private Integer qtyDispensed;

    private Boolean missedDose;

    private Boolean adverseIssues;

    private Boolean fromServer;

    private Boolean synced;

    @ManyToOne
    private Patient patient;

    @EntityView(Refill.class)
    @CreatableEntityView
    public interface CreateView {
        @IdMapping
        UUID getId();

        @NotNull
        LocalDate getDate();

        void setDate(LocalDate date);

        LocalDate getDateNextRefill();

        void setDateNextRefill(LocalDate date);

        @NotNull
        String getRegimen();

        void setRegimen(String regimen);

        @NotNull
        @Min(0)
        Integer getQtyPrescribed();

        void setQtyPrescribed(Integer qty);

        @NotNull
        @Min(0)
        Integer getQtyDispensed();

        void setQtyDispensed(Integer qty);

        Boolean getMissedDose();

        void setMissedDose(Boolean missedDose);

        Boolean getAdverseIssues();

        void setAdverseIssues(Boolean adverseIssues);

        Boolean getSynced();

        void setSynced(Boolean synced);

        Boolean getFromServer();

        void setFromServer(Boolean fromServer);

        Patient.IdView getPatient();

        void setPatient(Patient.IdView patient);

        @PostCreate
        default void postCreate() {
            setSynced(false);
            setFromServer(false);
        }
    }

    @EntityView(Refill.class)
    @UpdatableEntityView
    public interface UpdateView extends CreateView {
        void setId(UUID id);
    }
}
