package org.fhi360.plugins.impilo.domain.entities;

import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class IdMappings {
    @Id
    @UUIDV7
    private UUID id;

    @NotNull
    UUID remote;

    @NotNull
    UUID local;
}
