package org.fhi360.plugins.impilo.web.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class StockFulfill {
    String regimen;
    Long bottles;
    String batchNo;
    String barcode;
    LocalDate expirationDate;
}
