package org.fhi360.plugins.impilo.domain.entities;

import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The Prescription class represents a prescription entity from EHR with various attributes such as id, prescriptionId, medicineId,
 * frequencyId, time, and a many-to-one relationship with the Patient class.
 */
@Entity
@Getter
@Setter
@Table(name = "imp_prescription")
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
