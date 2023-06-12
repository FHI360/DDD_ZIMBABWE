package org.fhi360.plugins.impilo.domain.entities;

import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Prescription {
    @Id
    @UUIDV7
    private UUID id;

    private String prescriptionId;

    private String medicineId;

    private String frequencyId;

    private LocalDateTime time;

    @ManyToOne
    private Patient patient;
}
