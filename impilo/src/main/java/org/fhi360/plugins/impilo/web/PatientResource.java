package org.fhi360.plugins.impilo.web;

import io.github.jbella.snl.core.api.services.util.PagedResult;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.Patient;
import org.fhi360.plugins.impilo.domain.entities.Refill;
import org.fhi360.plugins.impilo.services.DevolveService;
import org.fhi360.plugins.impilo.services.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * The PatientResource class is a REST controller that handles requests related to patients and refills.
 */
@RestController
@RequestMapping("/api/impilo/patients")
@RequiredArgsConstructor
public class PatientResource {
    private final PatientService patientService;
    private final DevolveService devolveService;

    /**
     * The function returns a paged list of patients based on optional keyword, assigned status, start index, and page size
     * parameters.
     *
     * @param keyword The "keyword" parameter is an optional parameter used for searching patients based on a specific
     * keyword. It is not required, so it can be left empty if not needed.
     * @param assigned The "assigned" parameter is a boolean flag that indicates whether the patient is assigned or not. If
     * the value is true, it means the patient is assigned. If the value is false or not provided, it means the patient is
     * not assigned.
     * @param start The "start" parameter is used to specify the starting index of the list of patients to be returned. It
     * is an optional parameter and has a default value of 0 if not provided.
     * @param pageSize The `pageSize` parameter is used to specify the number of items to be displayed per page in the
     * returned result. It has a default value of 10, but can be overridden by providing a different value as a request
     * parameter.
     * @return The method is returning a PagedResult object of type Patient.ListView.
     */
    @GetMapping
    public PagedResult<Patient.ListView> list(@RequestParam(required = false) String keyword,
                                              @RequestParam(required = false) Boolean assigned,
                                              @RequestParam(required = false, defaultValue = "0") int start,
                                              @RequestParam(required = false, defaultValue = "10") int pageSize) {
        return devolveService.list(keyword, assigned, start, pageSize);
    }

    /**
     * The function devolve takes in two UUID parameters, organisationId and patientId, and returns a Boolean value.
     *
     * @param organisationId The `organisationId` parameter is a UUID (Universally Unique Identifier) that represents the
     * unique identifier of an organisation.
     * @param patientId The patientId parameter is a UUID (Universally Unique Identifier) that represents the unique
     * identifier of a patient.
     * @return A Boolean value is being returned.
     */
    @GetMapping("/devolve/patient/{patientId}/organisation/{organisationId}")
    public Boolean devolve(@PathVariable UUID organisationId, @PathVariable UUID patientId) {
        return devolveService.devolve(organisationId, patientId);
    }

    /**
     * The function unDevolve takes a patientId as input and returns a Boolean indicating whether the patient has been
     * unassigned.
     *
     * @param patientId The patientId is a unique identifier for a patient. It is of type UUID (Universally Unique
     * Identifier).
     * @return The method is returning a Boolean value.
     */
    @GetMapping("/unassign/patient/{patientId}")
    public Boolean unDevolve(@PathVariable UUID patientId) {
        return devolveService.unDevolve(patientId);
    }

    /**
     * The function retrieves a patient by their ID and returns a ResponseEntity containing the patient's information.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that represents the unique identifier of a
     * patient.
     * @return The method is returning a ResponseEntity object that wraps a Patient.View object.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient.View> getById(@PathVariable UUID id) {
        return ResponseEntity.of(patientService.getById(id));
    }

    /**
     * The above function is a GET request mapping that retrieves a list of refill objects based on the patient's ID.
     *
     * @param id The "id" parameter is a UUID (Universally Unique Identifier) that represents the unique identifier of a
     * patient.
     * @return The method is returning a list of objects of type `Refill.ListView`.
     */
    @GetMapping("/refills/patient/{id}")
    public List<Refill.ListView> getByPatient(@PathVariable UUID id) {
        return patientService.getByPatient(id);
    }
}
