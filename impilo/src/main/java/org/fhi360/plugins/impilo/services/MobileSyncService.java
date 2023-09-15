package org.fhi360.plugins.impilo.services;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.DevolveRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.domain.repositories.StockIssuanceRepository;
import org.fhi360.plugins.impilo.services.models.ARVRequest;
import org.fhi360.plugins.impilo.services.models.SyncData;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The `MobileSyncService` class is a Spring service that handles data synchronization by saving refill, clinic data,
 * devolve, and stock request information.
 */
@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
public class MobileSyncService {
    private final ClinicDataRepository clinicDataRepository;
    private final RefillRepository refillRepository;
    private final DevolveRepository devolveRepository;
    private final StockIssuanceRepository stockIssuanceRepository;
    private final StockRequestService stockRequestService;

    /**
     * The `sync` function saves various types of data (refills, clinic data, devolves, and requests) and returns true.
     *
     * @param data The `data` parameter is an object of type `SyncData`. It contains several lists of objects, including
     *             `refills`, `clinicData`, `devolves`, `requests` and `acknowledgements`.
     *             Each list contains specific data related to syncing information.
     * @return The method is returning a boolean value of `true`.
     */
    @Transactional
    public boolean sync(SyncData data) {
        data.getRefills()
            .forEach(r -> {
                r.setReference(UUID.randomUUID());
                refillRepository.save(r);
            });


        data.getClinicData()
            .forEach(r -> {
                r.setReference(UUID.randomUUID());
                clinicDataRepository.save(r);
            });

        data.getDevolves()
            .forEach(r -> {
                r.setReference(UUID.randomUUID());
                devolveRepository.save(r);
            });

        if (data.getRequests() != null && !data.getRequests().isEmpty()) {
            data.getRequests().forEach(request -> {
                var arv = new ARVRequest();
                arv.setArvDrug(request.getRegimen());
                arv.setBottles(request.getQuantity());
                stockRequestService.saveRequest(request.getDate(), request.getSiteCode(), request.getUniqueId(), arv);
            });
        }

        if (data.getAcknowledgements() != null) {
            data.getAcknowledgements().forEach(reference -> {
                stockIssuanceRepository.findByReference(reference).ifPresent(i -> {
                    i.setAcknowledged(true);
                    stockIssuanceRepository.save(i);
                });
            });
        }

        return true;
    }
}
