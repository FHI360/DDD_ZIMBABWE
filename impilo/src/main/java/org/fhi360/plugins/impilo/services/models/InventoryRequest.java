package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The InventoryRequest class is a Java class with getters and setters for properties such as regimen, quantity, date,
 * siteCode, and uniqueId.
 */
@Getter
@Setter
public class InventoryRequest {
    private String regimen;
    private Integer quantity;
    private LocalDate date;
    private UUID siteCode;
    private UUID uniqueId;
}
