package org.fhi360.plugins.impilo.services.model;

import lombok.Getter;
import lombok.Setter;
import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.fhi360.plugins.impilo.domain.entities.StockRequest;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class FacilityData {
    private List<Devolve.CreateView> devolves;
    private List<Refill.UpdateView> refills;
    private List<ClinicData.UpdateView> clinics;
    private List<StockRequest.UpdateView> stockRequest;

    private UUID reference;
}
