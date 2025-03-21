package org.fhi360.plugins.impilo.domain.repositories;

import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClinicDataRepository extends JpaRepository<ClinicData, UUID> {
    @Query("select r from ClinicData r where true = ?1 or r.synced = false ")
    List<ClinicData> findForSync(boolean all);

    @Modifying
    @Query("UPDATE ClinicData SET synced = true WHERE synced = false")
    void updateSyncStatus();

    Optional<ClinicData> findByReference(UUID reference);

    @Modifying
    @Query("update Refill set synced = true where id = ?1")
    void updateSyncStatus(UUID id);
}
