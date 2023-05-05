package org.fhi360.plugins.impilo.domain.repositories;

import org.fhi360.plugins.impilo.domain.entities.ClinicData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClinicDataRepository extends JpaRepository<ClinicData, UUID> {
}
