package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class EHRData {
    private UUID reference = UUID.randomUUID();
    private List<Dispense> dispenses;
    private List<Vitals> vitals;
    private List<Clinic> clinicData;
}
