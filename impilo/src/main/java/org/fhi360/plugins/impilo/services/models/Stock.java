package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class Stock {
    private LocalDate date;

    private String regimen;

    private Long bottles;

    private String batchNo;

    private String serialNo;

    private LocalDate expirationDate;

    private LocalDate manufactureDate;

    private String batchIssuanceId;
}
