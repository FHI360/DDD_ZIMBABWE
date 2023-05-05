package org.fhi360.plugins.impilo.web.models;

import lombok.Getter;
import lombok.Setter;
import org.fhi360.plugins.impilo.domain.entities.Patient;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ActivationData {
    private String site;
    private List<Patient.View> patients = new ArrayList<>();
}
