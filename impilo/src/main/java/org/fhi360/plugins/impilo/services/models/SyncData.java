package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;
import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.fhi360.plugins.impilo.domain.entities.Refill;

import java.util.List;
import java.util.UUID;

/**
 * The SyncData class is a Java class that contains lists of Refill, ClinicData, Devolve, and InventoryRequest objects, and
 * provides getter and setter methods for accessing and modifying these lists.
 */
@Getter
@Setter
public class SyncData {
    private List<Refill> refills;
    private List<ClinicData> clinicData;
    private List<Devolve> devolves;
    private List<InventoryRequest> requests;
    private List<UUID> acknowledgements;
}
