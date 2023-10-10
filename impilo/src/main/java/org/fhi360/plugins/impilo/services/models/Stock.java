package org.fhi360.plugins.impilo.services.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class Stock {
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDateTime date;

    @NotNull
    private String regimen;

    @NotNull
    private Long bottles;

    @NotNull
    private String batchNo;

    private String serialNo;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDate expirationDate;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDate manufactureDate;

    @NotNull
    private String batchIssuanceId;
}
