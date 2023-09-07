package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.*;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
public class StockRequest {
    @Id
    @UUIDV7
    private UUID id;

    @NotNull
    private LocalDate date;

    @NotNull
    private String arvDrug;

    @NotNull
    @Min(1)
    private Integer bottles;

    @NotNull
    private String requestId;

    private Boolean synced = false;

    private UUID reference;

    @NotNull
    @ManyToOne
    private Organisation site;

    @EntityView(StockRequest.class)
    public interface IdView {
        @IdMapping
        UUID getId();
    }

    @EntityView(StockRequest.class)
    @CreatableEntityView
    public interface CreateView extends IdView {
        LocalDate getDate();

        void setDate(LocalDate date);

        String getArvDrug();

        void setArvDrug(String drug);

        Integer getBottles();

        void setBottles(Integer bottles);

        String getRequestId();

        void setRequestId(String id);

        Organisation.ShortView getSite();

        void setSite(Organisation.ShortView site);

        Boolean getSynced();

        void setSynced(Boolean synced);

        UUID getReference();

        void setReference(UUID reference);

        @PostCreate
        default void postCreate() {
            setSynced(false);
        }
    }

    @EntityView(StockRequest.class)
    @UpdatableEntityView
    public interface UpdateView extends CreateView {
        void setId(UUID id);
    }
}
