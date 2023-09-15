package org.fhi360.plugins.impilo.domain.entities;

import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * The class "IdMappings" is an entity class in Java that represents a mapping between a remote UUID and a local UUID.
 */
@Entity
@Getter
@Setter
@Table(name = "imp_id_mappings")
public class IdMappings {
    @Id
    @UUIDV7
    private UUID id;

    @NotNull
    UUID remote;

    @NotNull
    UUID local;
}
