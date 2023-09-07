package org.fhi360.plugins.impilo.services.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class InventoryRequest {
    private String regimen;
    private Integer quantity;
    private LocalDate date;
    private UUID siteCode;
    private String uniqueId;
}
