package org.fhi360.plugins.impilo.web;

import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.fhi360.plugins.impilo.services.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/impilo/patients")
@RequiredArgsConstructor
public class PatientResource {
    private final PatientService patientService;

    @GetMapping("/{id}")
    public ResponseEntity<Patient.View> getById(@PathVariable UUID id) {
        return ResponseEntity.of(patientService.getById(id));
    }

    @GetMapping("/refills/patient/{id}")
    public List<Refill.ListView> getByPatient(@PathVariable UUID id) {
        return patientService.getByPatient(id);
    }
}
