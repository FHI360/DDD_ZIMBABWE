package org.fhi360.plugins.impilo.domain.repositories;

import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefillRepository extends JpaRepository<Refill, UUID> {
    List<Refill> findByPatient(Patient patient);

    List<Refill> findBySyncedIsFalse();

    Optional<Refill> findByReference(UUID reference);

    @Modifying
    @Query("UPDATE Refill SET synced = true WHERE synced = false")
    public void updateSyncStatus();
}
