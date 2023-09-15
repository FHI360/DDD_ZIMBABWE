package org.fhi360.plugins.impilo.services.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ARVRequest {
    @NotEmpty
    private String arvDrug;
    @NotNull
    @Min(1)
    private Integer bottles;
}
