package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Prescription {
    private String prescriptionId;

    private String medicineId;

    private String frequencyId;
    private String medicineName;

    private LocalDateTime time;
}
