package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.*;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The StockIssuance class represents the issuance of a stock item, including details such as the stock item, quantity,
 * date, acknowledgement status, batch issuance ID, reference, synchronization status, site, and request.
 */
@Entity
@Getter
@Setter
@Table(name = "imp_stock_issuance")
public class StockIssuance {
    @Id
    @UUIDV7
    private UUID id;

    @ManyToOne
    private Stock stock;

    private Long bottles;

    private LocalDate date;

    private Boolean acknowledged;

    private String batchIssuanceId;

    private UUID reference;

    private Boolean synced = false;

    @ManyToOne
    private Organisation site;

    @ManyToOne
    private StockRequest request;

    @EntityView(StockIssuance.class)
    @CreatableEntityView
    @UpdatableEntityView
    public interface CreateView {
        @IdMapping
        UUID getId();

        void setId(UUID id);

        @NotNull
        Long getBottles();

        void setBottles(Long bottles);

        LocalDate getDate();

        void setDate(LocalDate date);

        UUID getReference();

        void setReference(UUID reference);

        Boolean getSynced();

        void setSynced(Boolean synced);

        @NotNull
        Stock.IdView getStock();

        void setStock(Stock.IdView stock);

        @NotNull
        Organisation.IdView getSite();

        void setSite(Organisation.IdView site);

        @NotNull
        StockRequest.IdView getRequest();

        void setRequest(StockRequest.IdView request);

        @PrePersist
        default void prePersist() {
            setSynced(false);
        }
    }

    @EntityView(StockIssuance.class)
    public record View(
        @IdMapping
        UUID id,
        Long bottles,
        LocalDate date,
        Boolean acknowledged,
        Stock.View stock,
        @Mapping("request.reference")
        UUID requestReference,
        UUID reference,
        String batchIssuanceId,
        Organisation.ShortView site) {
    }
}
