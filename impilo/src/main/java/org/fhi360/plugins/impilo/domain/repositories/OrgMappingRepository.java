package org.fhi360.plugins.impilo.domain.repositories;

import jakarta.validation.constraints.NotNull;
import org.fhi360.plugins.impilo.domain.entities.OrgMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrgMappingRepository extends JpaRepository<OrgMapping, UUID> {
    Optional<OrgMapping> findByRemote(@NotNull UUID remote);
    Optional<OrgMapping> findByLocal(@NotNull UUID local);
}
