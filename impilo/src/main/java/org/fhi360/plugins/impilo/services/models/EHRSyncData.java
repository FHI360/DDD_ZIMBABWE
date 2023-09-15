package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EHRSyncData {
    private List<Patient> patients;
    private List<Stock> stocks;
}
