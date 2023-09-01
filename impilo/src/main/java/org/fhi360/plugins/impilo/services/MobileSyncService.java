package org.fhi360.plugins.impilo.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fhi360.plugins.impilo.domain.repositories.ClinicDataRepository;
import org.fhi360.plugins.impilo.domain.repositories.DevolveRepository;
import org.fhi360.plugins.impilo.domain.repositories.RefillRepository;
import org.fhi360.plugins.impilo.services.model.SyncData;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@Slf4j
public class MobileSyncService {
    private final ClinicDataRepository clinicDataRepository;
    private final RefillRepository refillRepository;
    private final DevolveRepository devolveRepository;

    @Transactional
    public boolean sync(SyncData data) {
        var refills = data.getRefills().stream()
                .map(r-> {
                    r.setReference(UUID.randomUUID());
                    return r;
                }).toList();
        refillRepository.saveAll(refills);

        var clinics = data.getClinicData().stream()
            .map(r-> {
                r.setReference(UUID.randomUUID());
                return r;
            }).toList();
        clinicDataRepository.saveAll(clinics);

        var devolves = data.getDevolves().stream()
            .map(r-> {
                r.setReference(UUID.randomUUID());
                return r;
            }).toList();
        devolveRepository.saveAll(devolves);

        return true;
    }
}
