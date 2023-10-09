package org.fhi360.plugins.impilo.services.models;

import io.github.jbella.snl.core.api.domain.Organisation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.fhi360.plugins.impilo.domain.entities.*;
import org.fhi360.plugins.impilo.domain.entities.Prescription;

import java.util.List;

/**
 * The ServerData class is a Java class that contains lists of various update and create views related to organisations,
 * patients, devolves, stock issuance, stocks, and stock requests.
 */
@Getter
@Setter
@ToString
public class ServerData {
    private List<Organisation.UpdateView> outlets;
    private List<Organisation.UpdateView> facilities;
    private List<org.fhi360.plugins.impilo.domain.entities.Patient.UpdateView> patients;
    private List<Devolve.CreateView> devolves;
    private List<StockIssuance.CreateView> stockIssuance;
    private List<org.fhi360.plugins.impilo.domain.entities.Stock.CreateView> stocks;
    private List<StockRequest.UpdateView> stockRequests;
    private List<Prescription.CreateView> prescriptions;
}
