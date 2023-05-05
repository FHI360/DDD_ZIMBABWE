package org.fhi360.plugins.impilo.domain.entities;

import com.blazebit.persistence.view.*;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.id.UUIDV7;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import org.fhi360.plugins.impilo.domain.providers.StockIssuedSubqueryProvider;

import java.time.LocalDate;
import java.util.UUID;

@Entity
public class Stock {
    @Id
    @UUIDV7
    private UUID id;

    private LocalDate date;

    private String regimen;

    private Long bottles;

    private String batchNo;

    private String serialNo;

    private LocalDate expirationDate;

    private LocalDate manufactureDate;

    @ManyToOne
    private Organisation facility;

    @EntityView(Stock.class)
    public interface IdView {
        @IdMapping
        UUID getId();
    }

    @EntityView(Stock.class)
    @CreatableEntityView
    @UpdatableEntityView
    public interface CreateView {
        @IdMapping
        UUID getId();

        void setId(UUID id);

        LocalDate getDate();

        void setDate(LocalDate date);

        String getBatchNo();

        void setBatchNo(String batchNo);

        String getSerialNo();

        void setSerialNo(String serialNo);

        LocalDate getManufactureDate();

        void setManufactureDate(LocalDate manufacturerDate);

        LocalDate getExpirationDate();

        void setExpirationDate(LocalDate expirationDate);

        String getRegimen();

        void setRegimen(String regimen);

        Long getBottles();

        void setBottles(Long bottles);

        Organisation.IdView getFacility();

        void setFacility(Organisation.IdView facility);
    }

    @EntityView(Stock.class)
    public record View(
        @IdMapping
        UUID id,
        LocalDate date,
        String batchNo,
        String serialNo,
        LocalDate manufactureDate,
        LocalDate expirationDate,
        String regimen,
        Long bottles,
        @MappingSubquery(StockIssuedSubqueryProvider.class)
        Long issued,

        @Mapping("facility.name")
        String facility
    ) {
    }
}
