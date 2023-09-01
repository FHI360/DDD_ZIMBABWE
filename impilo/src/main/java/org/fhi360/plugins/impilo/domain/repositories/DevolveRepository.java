package org.fhi360.plugins.impilo.domain.repositories;

import org.fhi360.plugins.impilo.domain.entities.Devolve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DevolveRepository extends JpaRepository<Devolve, UUID> {
    Optional<Devolve> findByReference(UUID reference);
}
