package org.fhi360.plugins.impilo.services.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Vitals {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateTime;
    private Integer diastolic;
    private Integer systolic;
    private String personId;
    private String vitalName = "BLOOD_PRESSURE";

    public boolean hasData() {
        return diastolic != null || systolic != null;
    }
}
