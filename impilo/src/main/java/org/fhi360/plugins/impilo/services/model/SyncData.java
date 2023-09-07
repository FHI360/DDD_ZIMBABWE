package org.fhi360.plugins.impilo.services.model;

import lombok.Getter;
import lombok.Setter;
import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.fhi360.plugins.impilo.domain.entities.Refill;

import java.util.List;

@Getter
@Setter
public class SyncData {
    private List<Refill> refills;
    private List<ClinicData> clinicData;
    private List<Devolve> devolves;
    private List<InventoryRequest> requests;
}
