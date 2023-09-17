package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Dispense {
    private LocalDate dateCreated;
    private String batchIssueId;
    private String facilityId;
    private String personId;
    private Integer quantity;
    private String frequencyId;
    private String medicineId;
    private String prescriptionId;
}
