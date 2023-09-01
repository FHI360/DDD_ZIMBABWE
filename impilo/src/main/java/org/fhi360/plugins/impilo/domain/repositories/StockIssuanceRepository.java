package org.fhi360.plugins.impilo.domain.repositories;

import org.fhi360.plugins.impilo.domain.entities.StockIssuance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockIssuanceRepository extends JpaRepository<StockIssuance, UUID> {
    Optional<StockIssuance> findByReference(UUID reference);
}
