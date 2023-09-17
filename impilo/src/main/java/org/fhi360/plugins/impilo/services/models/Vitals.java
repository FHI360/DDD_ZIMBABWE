package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Vitals {
    private LocalDateTime dateTime;
    private Integer diastolic;
    private Integer systolic;
    private String personId;
    private String vitalName = "BLOOD_PRESSURE";
}
