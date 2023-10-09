package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.CreatableEntityView;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import com.blazebit.persistence.view.UpdatableEntityView;
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

    private Integer prescribedQty;

    private LocalDateTime time;

    @ManyToOne
    private Patient patient;

    @EntityView(Prescription.class)
    @UpdatableEntityView
    @CreatableEntityView
    public interface CreateView {
        @IdMapping
        UUID getId();

        void setId(UUID id);

        String getPrescriptionId();

        void setPrescriptionId(String prescriptionId);

        String getMedicineId();

        void setMedicineId(String medicineId);

        String getFrequencyId();

        void setFrequencyId(String frequencyId);

        Integer getPrescribedQty();

        void setPrescribedQty(Integer prescribedQty);

        LocalDateTime getTime();

        void setTime(LocalDateTime time);

        Patient.IdView getPatient();

        void setPatient(Patient.IdView patient);
    }
}
