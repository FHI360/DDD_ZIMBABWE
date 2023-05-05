package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.CreatableEntityView;
import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.IdMapping;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class StockIssuance {
    @Id
    @UUIDV7
    private UUID id;

    @ManyToOne
    private Stock stock;

    private Long bottles;

    private LocalDate date;

    private Boolean acknowledged;

    @ManyToOne
    private Organisation site;

    @EntityView(StockIssuance.class)
    @CreatableEntityView
    public interface CreateView {
        @IdMapping
        UUID getId();

        void setId(UUID id);

        @NotNull
        Long getBottles();

        void setBottles(Long bottles);

        LocalDate getDate();

        void setDate(LocalDate date);

        @NotNull
        Stock.IdView getStock();

        void setStock(Stock.IdView stock);

        @NotNull
        Organisation.IdView getSite();

        void setSite(Organisation.IdView site);
    }

    @EntityView(StockIssuance.class)
    public record View(
        @IdMapping
        UUID id,
        Long bottles,
        LocalDate date,
        Boolean acknowledged,
        Stock.View stock,
        Organisation.ShortView site) {
    }
}
