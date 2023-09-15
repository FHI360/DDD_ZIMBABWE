package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class StockFulfill {
    String regimen;
    Long bottles;
    String batchNo;
    String barcode;
    LocalDate expirationDate;
    UUID reference;
    UUID requestReference;
    String batchIssueId;
}
