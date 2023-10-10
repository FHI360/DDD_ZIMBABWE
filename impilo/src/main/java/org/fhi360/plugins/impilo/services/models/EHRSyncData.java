package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EHRSyncData {
    private List<Patient> patients;
    private List<Stock> stocks;
}
