package org.fhi360.plugins.impilo.domain.repositories;

import jakarta.validation.constraints.NotNull;
import org.fhi360.plugins.impilo.domain.entities.IdMappings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IdMappingRepository extends JpaRepository<IdMappings, UUID> {
    Optional<IdMappings> findByRemote(@NotNull UUID remote);
    Optional<IdMappings> findByLocal(@NotNull UUID local);
}
