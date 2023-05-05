package org.fhi360.plugins.impilo.web.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Getter
@Setter
@Service
public class DiscontinuationData {
    @NotNull
    private LocalDate date;
    @NotBlank
    private String reason;
}
