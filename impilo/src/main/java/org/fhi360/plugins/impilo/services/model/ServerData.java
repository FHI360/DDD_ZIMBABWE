package org.fhi360.plugins.impilo.services.model;

import io.github.jbella.snl.core.api.domain.Organisation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.fhi360.plugins.impilo.domain.entities.*;

import java.util.List;

@Getter
@Setter
@ToString
public class ServerData {
    private List<Organisation.UpdateView> outlets;
    private List<Organisation.UpdateView> facilities;
    private List<Patient.UpdateView> patients;
    private List<Devolve.CreateView> devolves;
    private List<StockIssuance.CreateView> stockIssuance;
    private List<Stock.CreateView> stocks;
    private List<StockRequest.UpdateView> stockRequests;
}
