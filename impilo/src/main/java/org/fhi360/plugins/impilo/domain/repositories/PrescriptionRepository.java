package org.fhi360.plugins.impilo.domain.repositories;

import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    Optional<Prescription> findFirstByPatientOrderByTimeDesc(Patient patient);

    Optional<Prescription> findByPrescriptionIdAndPatient(String prescriptionId, Patient patient);
}
