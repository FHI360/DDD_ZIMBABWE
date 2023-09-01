package org.fhi360.plugins.impilo.services;

import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jbella.snl.core.api.domain.Organisation;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.domain.entities.*;
import org.fhi360.plugins.impilo.domain.repositories.DevolveRepository;
import org.fhi360.plugins.impilo.domain.repositories.OrgMappingRepository;
import org.fhi360.plugins.impilo.domain.repositories.PatientRepository;
import org.fhi360.plugins.impilo.domain.repositories.StockIssuanceRepository;
import org.fhi360.plugins.impilo.services.model.FacilityData;
import org.fhi360.plugins.impilo.services.model.ServerData;
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
public class CentralServerService {
    private final EntityManager em;
    private final CriteriaBuilderFactory cbf;
    private final EntityViewManager evm;
    private final ObjectMapper objectMapper;
    private final PatientRepository patientRepository;
    private final StockIssuanceRepository stockIssuanceRepository;
    private final DevolveRepository devolveRepository;
    private final OrgMappingRepository mappingRepository;
    private final Map<UUID, FacilityData> acknowledgment = new HashMap<>();

    @Transactional
    public boolean saveData(ServerData data) {
        data.getFacilities().forEach(this::saveOrUpdateOrganisation);
        data.getOutlets().forEach(this::saveOrUpdateOrganisation);

        data.getPatients().forEach(c -> {
            var _patient = objectMapper.convertValue(c, Patient.class);
            mappingRepository.findByLocal(c.getOrganisation().getId()).ifPresent(mapping -> {
                c.setOrganisation(getOrganisationIdView(mapping.getRemote()));
            });
            var patient = patientRepository.findByReference(c.getReference()).orElse(_patient);
            BeanUtils.copyProperties(_patient, patient, "id");

            OrgMapping mapping = new OrgMapping();
            mapping.setRemote(c.getId());
            patient = patientRepository.save(patient);
            mapping.setLocal(patient.getId());
            mappingRepository.save(mapping);
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

        var issues = data.getStockIssuance().stream()
            .map(c -> {
                var _issuance = objectMapper.convertValue(c, StockIssuance.class);
                var issuance = stockIssuanceRepository.findByReference(c.getReference()).orElse(_issuance);
                BeanUtils.copyProperties(_issuance, issuance, "id");
                return issuance;
            })
            .toList();
        stockIssuanceRepository.saveAll(issues);


        return true;
    }

    @Transactional
    public FacilityData retrieveFacilityData(String facilityId, List<UUID> sites) {
        FacilityData facilityData = new FacilityData();

        var settings1 = EntityViewSetting.create(Devolve.CreateView.class);
        var cb1 = cbf.create(em, Devolve.class)
            .where("patient.facilityId").eq(facilityId)
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
            .where("patient.facilityId").eq(facilityId)
            .where("synced").eq(false);
        List<Refill.UpdateView> refills = evm.applySetting(settings2, cb2).getResultList().stream()
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
        facilityData.setRefills(refills);

        var settings3 = EntityViewSetting.create(ClinicData.UpdateView.class);
        var cb3 = cbf.create(em, ClinicData.class)
            .where("patient.facilityId").eq(facilityId)
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

        if (sites != null && !sites.isEmpty()) {
            var settings4 = EntityViewSetting.create(StockRequest.UpdateView.class);
            var cb4 = cbf.create(em, StockRequest.class)
                .where("site.id").in(sites)
                .where("synced").eq(false);
            List<StockRequest.UpdateView> requests = evm.applySetting(settings4, cb4).getResultList().stream()
                .map(d -> {
                    mappingRepository.findByLocal(d.getSite().getId()).ifPresent(mapping -> {
                        d.setSite(evm.convert(getOrganisationIdView(mapping.getRemote()), Organisation.ShortView.class));
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

    private void saveOrUpdateOrganisation(Organisation.CreateView organisation) {
        UUID remoteId = organisation.getId();
        UUID localId = mappingRepository.findByRemote(remoteId).map(OrgMapping::getLocal).orElse(null);
        if (localId == null) {//New organisation at Central Server
            var party = organisation.getParty();
            party.setAddresses(
                party.getAddresses().stream()
                    .map(a -> {
                        a.setId(null);
                        return a;
                    })
                    .collect(Collectors.toSet())
            );
            party.setIdentifiers(
                party.getIdentifiers().stream()
                    .map(a -> {
                        a.setId(null);
                        return a;
                    })
                    .collect(Collectors.toSet())
            );
            organisation.setParty(party);
            evm.save(em, organisation);

            OrgMapping mapping = new OrgMapping();
            mapping.setRemote(remoteId);
            mapping.setLocal(organisation.getId());
            mappingRepository.save(mapping);
        }
    }

    private Patient.IdView getPatientIdView(UUID id) {
        var settings = EntityViewSetting.create(Patient.IdView.class);
        var cb = cbf.create(em, Patient.class).where("id").eq(id);
        return evm.applySetting(settings, cb).getSingleResult();
    }

    private Organisation.IdView getOrganisationIdView(UUID id) {
        var settings = EntityViewSetting.create(Organisation.IdView.class);
        var cb = cbf.create(em, Organisation.class).where("id").eq(id);
        return evm.applySetting(settings, cb).getSingleResult();
    }
}
