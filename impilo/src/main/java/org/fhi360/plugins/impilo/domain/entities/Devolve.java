package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.CreatableEntityView;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.PrePersist;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The Devolve class represents a devolve entity with various attributes such as id, date, reasonDiscontinued, synced,
 * reference, organisation, and patient.
 */
@Entity
@Getter
@Setter
@Table(name = "imp_devolve")
public class Devolve {
    @Id
    @UUIDV7
    private UUID id;

    @NotNull
    private LocalDateTime date;

    private String reasonDiscontinued;

    private Boolean synced = false;

    private UUID reference;

    @ManyToOne
    private Organisation organisation;

    @ManyToOne
    private Patient patient;

    @EntityView(Devolve.class)
    @CreatableEntityView
    public interface CreateView {
        @IdMapping
        UUID getId();

        Boolean getSynced();

        LocalDateTime getDate();
        void setDate(LocalDateTime date);

        String getReasonDiscontinued();

        void setReasonDiscontinued(String reason);

        void setSynced(Boolean synced);

        UUID getReference();

        void setReference(UUID reference);

        Organisation.IdView getOrganisation();

        void setOrganisation(Organisation.IdView organisation);

        Patient.IdView getPatient();

        void setPatient(Patient.IdView patient);

        @PrePersist
        default void prePersist() {
            setSynced(false);
        }
    }
}
