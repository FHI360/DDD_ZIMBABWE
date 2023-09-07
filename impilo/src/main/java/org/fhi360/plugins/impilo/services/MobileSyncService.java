package org.fhi360.plugins.impilo.services;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.DevolveRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.services.model.SyncData;
import org.fhi360.plugins.impilo.web.models.ARVRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class MobileSyncService {
    private final ClinicDataRepository clinicDataRepository;
    private final RefillRepository refillRepository;
    private final DevolveRepository devolveRepository;
    private final StockRequestService stockRequestService;

    @Transactional
    public boolean sync(SyncData data) {
        var refills = data.getRefills().stream()
            .map(r -> {
                r.setReference(UUID.randomUUID());
                return r;
            }).toList();
        refillRepository.saveAll(refills);

        var clinics = data.getClinicData().stream()
            .map(r -> {
                r.setReference(UUID.randomUUID());
                return r;
            }).toList();
        clinicDataRepository.saveAll(clinics);

        var devolves = data.getDevolves().stream()
            .map(r -> {
                r.setReference(UUID.randomUUID());
                return r;
            }).toList();
        devolveRepository.saveAll(devolves);

        if (data.getRequests() != null && !data.getRequests().isEmpty()) {
            data.getRequests().forEach(request -> {
                var arv = new ARVRequest();
                arv.setArvDrug(request.getRegimen());
                arv.setBottles(request.getQuantity());
                stockRequestService.saveRequest(request.getDate(), request.getSiteCode(), request.getUniqueId(), arv);
            });
        }

        return true;
    }
}
