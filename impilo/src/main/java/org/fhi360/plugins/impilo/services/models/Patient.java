package org.fhi360.plugins.impilo.services.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Patient {
    @NotNull
    private String givenName;

    @NotNull
    private String familyName;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private String sex;

    private String phoneNumber;

    private String address;

    @NotNull
    private String hospitalNumber;

    private String regimen;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate nextAppointmentDate;

    @NotNull
    private String personId;

    @NotNull
    private UUID patientId;

    private String facilityId;

    private String facilityName;

    private List<Prescription> prescriptions;
}
