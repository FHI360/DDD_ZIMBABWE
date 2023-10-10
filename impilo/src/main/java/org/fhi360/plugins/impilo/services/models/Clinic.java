package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Clinic {
    private LocalDate date;

    private String personId;

    private Float weight;

    private Float temperature;

    private Boolean coughing;

    private Boolean sweating;

    private Boolean weightLoss;

    private Boolean swelling;

    private Boolean fever;

    private Boolean tbReferred;

    public boolean hasData() {
        return weight != null || temperature != null || coughing != null || sweating != null || weightLoss != null
            || swelling != null || fever != null || tbReferred != null;
    }
}
