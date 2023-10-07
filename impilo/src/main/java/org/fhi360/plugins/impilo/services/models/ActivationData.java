package org.fhi360.plugins.impilo.services.models;

import lombok.Getter;
import lombok.Setter;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.fhi360.plugins.impilo.domain.entities.StockRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActivationData {
    private String site;
    private List<Patient.View> patients = new ArrayList<>();
    private List<Refill.SyncView> refills = new ArrayList<>();
    private List<StockFulfill> stockIssues = new ArrayList<>();
    private List<StockRequest.SyncView> requests = new ArrayList<>();
}
