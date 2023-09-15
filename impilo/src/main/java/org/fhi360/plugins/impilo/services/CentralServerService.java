/**
 * The `CentralServerService` class is a service that handles saving and retrieving data from a central server in a
 * Impilo gateway application.
 */
package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.ConvertOption;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jbella.snl.core.api.domain.Address;
import io.github.jbella.snl.core.api.domain.Identifier;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.domain.Party;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fhi360.plugins.impilo.domain.entities.*;
import org.fhi360.plugins.impilo.domain.repositories.*;
import org.fhi360.plugins.impilo.services.models.FacilityData;
import org.fhi360.plugins.impilo.services.models.ServerData;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
@Slf4j
public class CentralServerService {
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final EntityViewManager evm;
    private final ObjectMapper objectMapper;
    private final PatientRepository patientRepository;
    private final StockIssuanceRepository stockIssuanceRepository;
    private final DevolveRepository devolveRepository;
    private final IdMappingRepository mappingRepository;
    private final StockRepository stockRepository;
    private final StockRequestRepository stockRequestRepository;
    private final Map<UUID, FacilityData> acknowledgment = new HashMap<>();

    /**
     * The `saveData` function saves various data objects to their respective repositories, including patients, devolves,
     * stocks, stock requests, and stock issuances.
     *
     * @param data The `data` parameter is an object of type `ServerData`. It contains various lists of objects such as
     * facilities, outlets, patients, devolves, stocks, stock requests, and stock issuance. The method performs various
     * operations on these objects, such as saving or updating them in the corresponding repositories.
     * @return The method is returning a boolean value of `true`.
     */
    @Transactional
    public boolean saveData(ServerData data) {
        data.getFacilities().forEach(this::saveOrUpdateOrganisation);
        data.getOutlets().forEach(this::saveOrUpdateOrganisation);

        data.getPatients().forEach(c -> {
            mappingRepository.findByRemote(c.getOrganisation().getId()).ifPresent(mapping -> {
                c.setOrganisation(getOrganisationIdView(mapping.getLocal()));
            });
            var _patient = objectMapper.convertValue(c, Patient.class);
            var patient = patientRepository.findByReference(c.getReference()).orElse(_patient);
            BeanUtils.copyProperties(_patient, patient, "id");

            if (mappingRepository.findByRemote(c.getId()).isEmpty()) {
                IdMappings mapping = new IdMappings();
                mapping.setRemote(c.getId());
                patient = patientRepository.save(patient);
                mapping.setLocal(patient.getId());
                mappingRepository.save(mapping);
            } else {
                patientRepository.save(patient);
            }
        });

        var devolves = data.getDevolves().stream()
            .map(c -> {
                mappingRepository.findByRemote(c.getPatient().getId()).ifPresent(mapping -> {
                    c.setPatient(getPatientIdView(mapping.getLocal()));
                });
                mappingRepository.findByRemote(c.getOrganisation().getId()).ifPresent(mapping -> {
                    c.setOrganisation(getOrganisationIdView(mapping.getLocal()));
                });
                var _devolve = objectMapper.convertValue(c, Devolve.class);
                _devolve.setSynced(true);
                var devolve = devolveRepository.findByReference(c.getReference()).orElse(_devolve);
                BeanUtils.copyProperties(_devolve, devolve, "id");
                return devolve;
            })
            .toList();
        devolveRepository.saveAll(devolves);

        data.getStocks().forEach(c -> {
            mappingRepository.findByRemote(c.getFacility().getId()).ifPresent(mapping -> {
                c.setFacility(getOrganisationIdView(mapping.getLocal()));
            });
            var _stock = objectMapper.convertValue(c, Stock.class);
            var stock = stockRepository.findByReference(c.getReference()).orElse(_stock);
            BeanUtils.copyProperties(_stock, stock, "id");

            if (mappingRepository.findByRemote(c.getId()).isEmpty()) {
                IdMappings mapping = new IdMappings();
                mapping.setRemote(c.getId());
                stock = stockRepository.save(stock);
                mapping.setLocal(stock.getId());
                mappingRepository.save(mapping);
            } else {
                stockRepository.save(stock);
            }
        });

        data.getStockRequests().forEach(c -> {
            if (mappingRepository.findByRemote(c.getId()).isEmpty()) {
                IdMappings mapping = new IdMappings();
                mapping.setRemote(c.getId());
                stockRequestRepository.findByReference(c.getReference()).ifPresent(req -> mapping.setLocal(req.getId()));
                mappingRepository.save(mapping);
            }
        });

        data.getStockIssuance().forEach(c -> {
            StockIssuance.CreateView issuance = evm.create(StockIssuance.CreateView.class);
            var settings = EntityViewSetting.create(StockIssuance.CreateView.class);
            var cb = cbf.create(em, StockIssuance.class)
                .where("reference").eq(c.getReference());
            try {
                issuance = evm.applySetting(settings, cb).getSingleResult();
            } catch (Exception ignored) {
            }

            StockIssuance.CreateView finalIssuance = issuance;
            mappingRepository.findByRemote(c.getRequest().getId()).ifPresent(mapping -> {
                finalIssuance.setRequest(getStockRequestIdView(mapping.getLocal()));
            });
            mappingRepository.findByRemote(c.getSite().getId()).ifPresent(mapping -> {
                finalIssuance.setSite(getOrganisationIdView(mapping.getLocal()));
            });
            mappingRepository.findByRemote(c.getStock().getId()).ifPresent(mapping -> {
                finalIssuance.setStock(getStockIdView(mapping.getLocal()));
            });

            BeanUtils.copyProperties(c, finalIssuance, "id", "request", "site", "stock");
            finalIssuance.setSynced(true);

            evm.save(em, issuance);
        });

        return true;
    }

    /**
     * The function retrieves facility data based on the facility ID and a list of outlet IDs, and returns the data in a
     * FacilityData object.
     *
     * @param facilityId The facilityId parameter is a UUID (Universally Unique Identifier) that represents the ID of a
     * facility.
     * @param outletIds The "outletIds" parameter is a list of UUIDs representing the IDs of outletIds associated with the facility.
     * @return The method is returning an object of type FacilityData.
     */
    @Transactional
    public FacilityData retrieveFacilityData(UUID facilityId, List<UUID> outletIds) {
        facilityId = mappingRepository.findByRemote(facilityId).map(IdMappings::getLocal).orElse(facilityId);

        FacilityData facilityData = new FacilityData();

        var settings1 = EntityViewSetting.create(Devolve.CreateView.class);
        var cb1 = cbf.create(em, Devolve.class)
            .where("patient.organisation.id").eq(facilityId)
            .where("synced").eq(false);
        List<Devolve.CreateView> devolves = evm.applySetting(settings1, cb1).getResultList().stream()
            .map(d -> {
                mappingRepository.findByLocal(d.getPatient().getId()).ifPresent(mapping -> {
                    d.setPatient(getPatientIdView(mapping.getRemote()));
                });
                mappingRepository.findByLocal(d.getOrganisation().getId()).ifPresent(mapping -> {
                    d.setOrganisation(getOrganisationIdView(mapping.getRemote()));
                });
                return d;
            })
            .toList();
        facilityData.setDevolves(devolves);

        var settings2 = EntityViewSetting.create(Refill.UpdateView.class);
        var cb2 = cbf.create(em, Refill.class)
            .where("patient.organisation.id").eq(facilityId)
            .where("synced").eq(false);
        List<Refill.UpdateView> refills = evm.applySetting(settings2, cb2).getResultList().stream()
            .map(d -> {
                mappingRepository.findByLocal(d.getOrganisation().getId()).ifPresent(mapping -> {
                    d.setOrganisation(getOrganisationIdView(mapping.getRemote()));
                });
                mappingRepository.findByLocal(d.getPatient().getId()).ifPresent(mapping -> {
                    d.setPatient(getPatientIdView(mapping.getRemote()));
                });
                return d;
            })
            .toList();
        facilityData.setRefills(refills);

        var settings3 = EntityViewSetting.create(ClinicData.UpdateView.class);
        var cb3 = cbf.create(em, ClinicData.class)
            .where("patient.organisation.id").eq(facilityId)
            .where("synced").eq(false);
        List<ClinicData.UpdateView> clinics = evm.applySetting(settings3, cb3).getResultList().stream()
            .map(d -> {
                mappingRepository.findByLocal(d.getPatient().getId()).ifPresent(mapping -> {
                    d.setPatient(getPatientIdView(mapping.getRemote()));
                });
                mappingRepository.findByLocal(d.getOrganisation().getId()).ifPresent(mapping -> {
                    d.setOrganisation(getOrganisationIdView(mapping.getRemote()));
                });
                return d;
            })
            .toList();
        facilityData.setClinics(clinics);

        if (outletIds != null && !outletIds.isEmpty()) {
            var _outletIds = outletIds.stream()
                .flatMap(id -> mappingRepository.findByRemote(id).stream())
                .map(IdMappings::getLocal)
                .toList();

            var settings4 = EntityViewSetting.create(StockRequest.UpdateView.class);
            var cb4 = cbf.create(em, StockRequest.class)
                .where("site.id").in(_outletIds)
                .where("synced").eq(false);
            List<StockRequest.UpdateView> requests = evm.applySetting(settings4, cb4).getResultList().stream()
                .map(d -> {
                    mappingRepository.findByLocal(d.getSite().getId()).ifPresent(mapping -> {
                        d.setSite(evm.convert(
                            getOrganisationIdView(mapping.getRemote()), Organisation.ShortView.class,
                            ConvertOption.IGNORE_MISSING_ATTRIBUTES));
                    });
                    return d;
                })
                .toList();
            facilityData.setStockRequest(requests);
        }

        UUID reference = UUID.randomUUID();
        facilityData.setReference(reference);
        acknowledgment.put(reference, facilityData);

        return facilityData;
    }

    /**
     * The `acknowledge` function updates the `synced` flag for various objects and removes the reference from the
     * acknowledgment map.
     *
     * @param reference The "reference" parameter is a UUID (Universally Unique Identifier) that is used to identify a
     * specific acknowledgment.
     */
    @Transactional
    public void acknowledge(UUID reference) {
        var data = acknowledgment.get(reference);
        if (data != null) {
            data.getRefills().forEach(o -> {
                o.setSynced(true);
                evm.save(em, o);
            });

            data.getClinics().forEach(o -> {
                o.setSynced(true);
                evm.save(em, o);
            });

            data.getDevolves().forEach(o -> {
                o.setSynced(true);
                evm.save(em, o);
            });

            data.getStockRequest().forEach(o -> {
                o.setSynced(true);
                evm.save(em, o);
            });

            acknowledgment.remove(reference);
        }
    }

    private void saveOrUpdateOrganisation(Organisation.UpdateView organisation) {
        UUID remoteId = organisation.getId();
        UUID localId = mappingRepository.findByRemote(remoteId).map(IdMappings::getLocal).orElse(null);
        if (localId == null) {//New organisation at Central Server
            Organisation.CreateView org = evm.create(Organisation.CreateView.class);
            BeanUtils.copyProperties(organisation, org, "id", "party");
            Party.PartyView party = evm.create(Party.PartyView.class);
            BeanUtils.copyProperties(organisation.getParty(), party, "id", "addresses", "identifiers");
            party.setAddresses(
                organisation.getParty().getAddresses().stream()
                    .map(a -> {
                        Address.AddressView address = evm.create(Address.AddressView.class);
                        BeanUtils.copyProperties(a, address, "id", "party");
                        return address;
                    })
                    .collect(Collectors.toSet())
            );
            party.setIdentifiers(
                organisation.getParty().getIdentifiers().stream()
                    .map(a -> {
                        Identifier.IdentifierView identifier = evm.create(Identifier.IdentifierView.class);
                        BeanUtils.copyProperties(a, identifier, "id", "party");
                        return identifier;
                    })
                    .collect(Collectors.toSet())
            );
            org.setParty(party);
            evm.save(em, org);

            if (mappingRepository.findByRemote(remoteId).isEmpty()) {
                IdMappings mapping = new IdMappings();
                mapping.setRemote(remoteId);
                mapping.setLocal(org.getId());
                mappingRepository.save(mapping);
            }
        }
    }

    private Patient.IdView getPatientIdView(UUID id) {
        Patient.UpdateView view = evm.create(Patient.UpdateView.class);
        view.setId(id);

        return evm.convert(view, Patient.IdView.class);
    }

    private Organisation.IdView getOrganisationIdView(UUID id) {
        Organisation.UpdateView view = evm.create(Organisation.UpdateView.class);
        view.setId(id);

        return evm.convert(view, Organisation.IdView.class);
    }

    private Stock.IdView getStockIdView(UUID id) {
        Stock.CreateView view = evm.create(Stock.CreateView.class);
        view.setId(id);

        return evm.convert(view, Stock.IdView.class);
    }

    private StockRequest.IdView getStockRequestIdView(UUID id) {
        StockRequest.UpdateView view = evm.create(StockRequest.UpdateView.class);
        view.setId(id);

        return evm.convert(view, StockRequest.IdView.class);
    }
}
