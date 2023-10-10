package org.fhi360.plugins.impilo.services.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class Prescription {
    @NotNull
    private String prescriptionId;

    @NotNull
    private String medicineId;

    @NotNull
    private String frequencyId;

    @NotNull
    private String medicineName;

    private Integer prescribedQty;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDateTime time;
}
