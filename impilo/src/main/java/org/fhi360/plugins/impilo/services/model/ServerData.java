package org.fhi360.plugins.impilo.services.model;

import io.github.jbella.snl.core.api.domain.Organisation;
import lombok.Getter;
import lombok.Setter;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.fhi360.plugins.impilo.domain.entities.StockIssuance;

import java.util.List;

@Getter
@Setter
public class ServerData {
    private List<Organisation.CreateView> outlets;
    private List<Organisation.CreateView> facilities;
    private List<Patient.CreateView> patients;
    private List<Devolve.CreateView> devolves;
    private List<StockIssuance.CreateView> stockIssuance;
}
