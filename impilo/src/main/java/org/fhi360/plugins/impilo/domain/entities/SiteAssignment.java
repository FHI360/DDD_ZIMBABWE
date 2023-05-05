package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.CreatableEntityView;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class SiteAssignment {
    @Id
    @UUIDV7
    private UUID id;

    @ManyToOne
    private Organisation site;

    @ManyToOne
    private Patient patient;

    @EntityView(SiteAssignment.class)
    @CreatableEntityView
    public interface View {
        @IdMapping
        UUID getId();

        Organisation.IdView getSite();

        void setSite(Organisation.IdView site);

        Patient.IdView getPatient();

        void setPatient(Patient.IdView patient);
    }
}
