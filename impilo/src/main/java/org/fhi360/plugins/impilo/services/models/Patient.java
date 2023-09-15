package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Patient {
    private String givenName;

    private String familyName;

    private LocalDate dateOfBirth;

    private String sex;

    private String phoneNumber;

    private String address;

    private String hospitalNumber;

    private String regimen;

    private LocalDate nextAppointmentDate;

    private String personId;

    private UUID patientId;

    private String facilityId;

    private String facilityName;

    private List<Prescription> prescriptions;
}
