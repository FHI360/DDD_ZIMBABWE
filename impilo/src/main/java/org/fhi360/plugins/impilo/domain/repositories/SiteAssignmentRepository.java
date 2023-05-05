package org.fhi360.plugins.impilo.domain.repositories;

import org.fhi360.plugins.impilo.domain.entities.SiteAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SiteAssignmentRepository extends JpaRepository<SiteAssignment, UUID> {
    @Modifying
    void deleteByPatientId(UUID patientId);
}
