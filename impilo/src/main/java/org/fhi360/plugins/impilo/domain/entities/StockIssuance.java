package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.*;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @NotNull
    @Min(1)
    private Integer bottles;

    @NotNull
    @Min(0)
    private Integer balance;

    private LocalDateTime date;

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
        Integer getBottles();

        void setBottles(Integer bottles);

        @NotNull
        Integer getBalance();

        void setBalance(Integer balance);

        LocalDateTime getDate();

        void setDate(LocalDateTime date);

        UUID getReference();

        void setReference(UUID reference);

        Boolean getSynced();

        void setSynced(Boolean synced);

        String getBatchIssuanceId();

        void  setBatchIssuanceId(String batchIssuanceId);

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
        Integer bottles,

        Integer balance,
        LocalDateTime date,
        Boolean acknowledged,
        Stock.View stock,
        @Mapping("request.reference")
        UUID requestReference,
        UUID reference,
        String batchIssuanceId,
        Organisation.ShortView site) {
    }
}
