package org.fhi360.plugins.impilo.services;

import com.aspose.cells.*;
import com.blazebit.persistence.view.EntityViewManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mattae.snl.plugins.security.extensions.AuthenticationServiceExtension;
import io.github.jbella.snl.core.api.domain.Organisation;
import io.github.jbella.snl.core.api.services.ExtensionService;
import jakarta.persistence.EntityManager;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IndicatorsReportService {
    private final EntityManager em;
    private final EntityViewManager evm;
    private final JdbcTemplate jdbcTemplate;
    private final ExtensionService extensionService;

    public static Long getValue(Map<String, Map<String, Map<String, List<DataElement>>>> data, String facility,
                                String outlet, String targetGroup, String key) {
        Long value = 0L;
        if (!data.containsKey(facility)) {
            return value;
        }
        if (!data.get(facility).containsKey(outlet)) {
            return value;
        }
        if (!data.get(facility).get(outlet).containsKey(targetGroup)) {
            return value;
        }
        switch (key) {
            case "fu" -> value = data.get(facility).get(outlet).get(targetGroup).get(0).fu;
            case "mu" -> value = data.get(facility).get(outlet).get(targetGroup).get(0).mu;
            case "ot" -> value = data.get(facility).get(outlet).get(targetGroup).get(0).ot;
        }

        return value != null ? value : 0;
    }

    public static Long getValue(Map<String, Map<String, List<DataElement>>> data,
                                String outlet, String targetGroup, String key) {
        Long value = 0L;
        if (!data.containsKey(outlet)) {
            return value;
        }
        if (!data.get(outlet).containsKey(targetGroup)) {
            return value;
        }
        switch (key) {
            case "fu" -> value = data.get(outlet).get(targetGroup).get(0).fu;
            case "mu" -> value = data.get(outlet).get(targetGroup).get(0).mu;
            case "ot" -> value = data.get(outlet).get(targetGroup).get(0).ot;
        }

        return value != null ? value : 0;
    }

    public static Long getValue(Map<String, List<DataElement>> data,
                                String targetGroup, String key) {
        Long value = 0L;
        if (!data.containsKey(targetGroup)) {
            return value;
        }
        switch (key) {
            case "fu" -> value = data.get(targetGroup).get(0).fu;
            case "mu" -> value = data.get(targetGroup).get(0).mu;
            case "ot" -> value = data.get(targetGroup).get(0).ot;
        }

        return value != null ? value : 0;
    }

    public ByteArrayOutputStream generateReport(LocalDate startDate, LocalDate endDate) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        String orgType = "CO";
        String orgName = "";
        Organisation.IdView organisation = extensionService.getPriorityExtension(AuthenticationServiceExtension.class)
            .organisation();
        if (organisation != null) {
            Organisation.ShortView organisationShort = evm.find(em, Organisation.ShortView.class, organisation.getId());
            orgType = organisationShort.getType();
            orgName = organisation.getName();
        }
        Workbook workbook = new Workbook();
        Worksheet sheet = workbook.getWorksheets().get(0);
        sheet.getCells().insertRow(0);
        Row row = sheet.getCells().getRows().get(0);
        Cell cell = row.get(0);
        cell.setValue(orgName);
        Style style = cell.getStyle();
        style.getFont().setBold(true);
        style.getFont().setSize(15);
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        cell.setStyle(style);
        sheet.getCells().insertRow(2);
        row = sheet.getCells().getRows().get(2);
        int titleIndex = row.getIndex();
        cell = row.get(0);
        cell.setValue("Summary Report");
        style = cell.getStyle();
        style.getFont().setBold(true);
        style.getFont().setSize(14);
        style.setHorizontalAlignment(TextAlignmentType.CENTER);
        cell.setStyle(style);
        sheet.getCells().insertRow(row.getIndex() + 1);
        row = sheet.getCells().getRows().get(row.getIndex() + 1);
        cell = row.get(0);
        cell.setValue("From");
        style = cell.getStyle();
        style.getFont().setBold(true);
        style.getFont().setSize(12);
        cell.setStyle(style);
        cell = row.get(1);
        cell.setValue(startDate.format(DateTimeFormatter.ISO_DATE));
        sheet.getCells().insertRow(row.getIndex() + 1);
        row = sheet.getCells().getRows().get(row.getIndex() + 1);
        cell = row.get(0);
        cell.setValue("To");
        style = cell.getStyle();
        style.getFont().setBold(true);
        style.getFont().setSize(12);
        cell.setStyle(style);
        cell = row.get(1);
        cell.setValue(endDate.format(DateTimeFormatter.ISO_DATE));
        Map<String, Object> data = new HashMap<>();
        if (Objects.equals(orgType, "CO")) {
            Map<String, Map<String, Map<String, List<DataElement>>>> missedAppointments = missedAppointmentsCountryView(startDate, endDate);
            Map<String, Map<String, Map<String, List<DataElement>>>> refills = refillsCountryView(startDate, endDate);
            Map<String, Map<String, Map<String, List<DataElement>>>> devolves = devolveCountryView(startDate, endDate);
            Map<String, Map<String, Map<String, List<DataElement>>>> appointments = appointmentCountryView(startDate, endDate);
            Map<String, Map<String, Map<String, List<DataElement>>>> discontinues = discontinueCountryView(startDate, endDate);

            Set<String> facilities = new HashSet<>();
            Set<String> outlets = new HashSet<>();
            Set<String> targetGroups = new HashSet<>();

            missedAppointments.forEach((f, v) -> {
                facilities.add(f);
                v.forEach((o, v2) -> {
                    outlets.add(o);
                    v2.forEach((t, v3) -> {
                        targetGroups.add(t);
                    });
                });
            });
            refills.forEach((f, v) -> {
                facilities.add(f);
                v.forEach((o, v2) -> {
                    outlets.add(o);
                    v2.forEach((t, v3) -> {
                        targetGroups.add(t);
                    });
                });
            });
            devolves.forEach((f, v) -> {
                facilities.add(f);
                v.forEach((o, v2) -> {
                    outlets.add(o);
                    v2.forEach((t, v3) -> {
                        targetGroups.add(t);
                    });
                });
            });
            appointments.forEach((f, v) -> {
                facilities.add(f);
                v.forEach((o, v2) -> {
                    outlets.add(o);
                    v2.forEach((t, v3) -> {
                        targetGroups.add(t);
                    });
                });
            });
            data.put("appointments", appointments);
            data.put("missedAppointments", missedAppointments);
            data.put("refills", refills);
            data.put("devolves", devolves);
            data.put("discontinues", discontinues);
            if (!targetGroups.isEmpty()) {
                buildFacilities(sheet, row.getIndex() + 2, 1, facilities, outlets, targetGroups, data);
            } else {
                targetGroups.add("");
                buildDisaggregations(sheet, row.getIndex() + 1, cell.getColumn() + 1, null, null, targetGroups, data);
            }

        } else if (Objects.equals(orgType, "FACILITY")) {
            Map<String, Map<String, List<DataElement>>> missedAppointments = missedAppointmentsFacilityView(organisation.getId(), startDate, endDate);
            Map<String, Map<String, List<DataElement>>> refills = refillsFacilityView(organisation.getId(), startDate, endDate);
            Map<String, Map<String, List<DataElement>>> devolves = devolveFacilityView(organisation.getId(), startDate, endDate);
            Map<String, Map<String, List<DataElement>>> appointments = appointmentFacilityView(organisation.getId(), startDate, endDate);
            Map<String, Map<String, List<DataElement>>> discontinues = discontinueFacilityView(organisation.getId(), startDate, endDate);
            Set<String> outlets = new HashSet<>();
            Set<String> targetGroups = new HashSet<>();

            missedAppointments.forEach((o, v2) -> {
                outlets.add(o);
                v2.forEach((t, v3) -> {
                    targetGroups.add(t);
                });
            });
            refills.forEach((o, v2) -> {
                outlets.add(o);
                v2.forEach((t, v3) -> {
                    targetGroups.add(t);
                });
            });
            devolves.forEach((o, v2) -> {
                outlets.add(o);
                v2.forEach((t, v3) -> {
                    targetGroups.add(t);
                });
            });
            appointments.forEach((o, v2) -> {
                outlets.add(o);
                v2.forEach((t, v3) -> {
                    targetGroups.add(t);
                });
            });

            data.put("appointments", appointments);
            data.put("missedAppointments", missedAppointments);
            data.put("refills", refills);
            data.put("devolves", devolves);
            data.put("discontinues", discontinues);
            if (!targetGroups.isEmpty()) {
                buildOutlets(sheet, row.getIndex() + 1, 1, null, outlets, targetGroups, data);
            } else {
                targetGroups.add("");
                buildDisaggregations(sheet, row.getIndex() + 1, cell.getColumn() + 1, null, null, targetGroups, data);
            }
        } else if (Objects.equals(orgType, "OUTLET")) {
            Map<String, List<DataElement>> missedAppointments = missedAppointmentsOutletView(organisation.getId(), startDate, endDate);
            Map<String, List<DataElement>> refills = refillsOutletView(organisation.getId(), startDate, endDate);
            Map<String, List<DataElement>> devolves = devolveOutletView(organisation.getId(), startDate, endDate);
            Map<String, List<DataElement>> appointments = appointmentOutletView(organisation.getId(), startDate, endDate);
            Map<String, List<DataElement>> discontinues = discontinueOutletView(organisation.getId(), startDate, endDate);
            Set<String> targetGroups = new HashSet<>();

            missedAppointments.forEach((t, v3) -> {
                targetGroups.add(t);
            });
            refills.forEach((t, v3) -> {
                targetGroups.add(t);
            });
            devolves.forEach((t, v3) -> {
                targetGroups.add(t);
            });
            appointments.forEach((t, v3) -> {
                targetGroups.add(t);
            });

            data.put("appointments", appointments);
            data.put("missedAppointments", missedAppointments);
            data.put("refills", refills);
            data.put("devolves", devolves);
            data.put("discontinues", discontinues);
            if (!targetGroups.isEmpty()) {
                buildDisaggregations(sheet, row.getIndex() + 1, 1, null, null, targetGroups, data);
            } else {
                targetGroups.add("");
                buildDisaggregations(sheet, row.getIndex() + 1, cell.getColumn() + 1, null, null, targetGroups, data);
            }
        }
        sheet.getCells().merge(titleIndex - 2, 0, 1,
            sheet.getCells().getMaxColumn() + 1);
        sheet.getCells().merge(titleIndex, 0, 1,
            sheet.getCells().getMaxColumn() + 1);
        sheet.setName("Summary Report");
        sheet.autoFitColumns(0, sheet.getCells().getMaxColumn());
        workbook.save(baos, SaveFormat.XLSX);
        baos.close();

        return baos;
    }

    public Map<String, List<DataElement>> discontinueOutletView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH CurrentDevolve AS (
             	SELECT * FROM (
             		SELECT o.id, date, d.patient_id, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                         ORDER BY date DESC, d.id DESC) rn FROM imp_devolve d JOIN fw_organisation o ON o.id =
                         d.organisation_id WHERE reason_discontinued IS NOT NULL AND date(date) BETWEEN ? AND ?
             	) d WHERE rn = 1 AND id = ?
             ),
            Appointment AS (
             	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
             		outlet FROM CurrentDevolve d JOIN imp_patient p ON d.patient_Id = p.id
             ),
            DISS AS (
                 SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                     CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                     CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                     outlet FROM Appointment a)
            SELECT SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
             """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)));
    }

    public Map<String, Map<String, List<DataElement>>> discontinueFacilityView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH CurrentDevolve AS (
            	SELECT * FROM (
            		SELECT f.id, date, d.patient_id, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_devolve d JOIN fw_organisation o ON o.id = d.organisation_id
                        JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id = p.organisation_id
            		WHERE reason_discontinued IS NOT NULL AND date(date) BETWEEN ? AND ?
            	) d WHERE rn = 1 AND id = ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, o.name facility FROM CurrentDevolve d JOIN imp_patient p ON d.patient_Id = p.id JOIN
            		fw_organisation o ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            GROUP BY 1 ORDER BY 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.outlet, Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup))));
    }

    public Map<String, Map<String, Map<String, List<DataElement>>>> discontinueCountryView(LocalDate start, LocalDate end) {
        String query = """
            WITH CurrentDevolve AS (
            	SELECT * FROM (
            		SELECT date, d.patient_id, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_devolve d JOIN fw_organisation o ON o.id = d.organisation_id
                        JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id = p.organisation_id
            		WHERE reason_discontinued IS NOT NULL AND date(date) BETWEEN ? AND ?
            	) d WHERE rn = 1
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, o.name facility FROM CurrentDevolve d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o
            		ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, facility, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            GROUP BY 1, 2 ORDER BY 2, 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class), start, end);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.facility, Collectors.groupingBy(r -> r.outlet,
                Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)))));
    }

    public Map<String, List<DataElement>> appointmentOutletView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT o.id, patient_Id, date, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_refill d JOIN fw_organisation o ON d.organisation_id
                        = o.id WHERE date_Next_Refill BETWEEN ? AND ?
                ) r WHERE rn = 1 AND id = ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
                    outlet, o.name facility FROM Data d JOIN imp_patient p ON d.patient_Id = p.id
            	JOIN fw_organisation o ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet FROM Appointment a)
            SELECT SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)));
    }

    public Map<String, Map<String, List<DataElement>>> appointmentFacilityView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT o.id, d.patient_Id, date, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_refill d RIGHT JOIN imp_patient p ON d.patient_id
                        = p.id JOIN fw_organisation o ON d.organisation_id = o.id WHERE date_Next_Refill BETWEEN ? AND ?
                ) r WHERE rn = 1 AND id = ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		 outlet, o.name facility FROM Data d JOIN imp_patient p ON d.patient_Id = p.id
            	JOIN fw_organisation o ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss GROUP BY 1 ORDER BY 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.outlet, Collectors.groupingBy(a -> a.targetGroup)));
    }

    public Map<String, Map<String, Map<String, List<DataElement>>>> appointmentCountryView(LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT d.patient_id, date, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_refill d RIGHT JOIN imp_patient p ON d.patient_id = p.id
                    JOIN fw_organisation o ON d.organisation_id = o.id WHERE date_Next_Refill BETWEEN ? AND ?
                ) r WHERE rn = 1
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		 outlet, o.name facility FROM Data d JOIN imp_patient p ON d.patient_Id = p.id
            	JOIN fw_organisation o ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT facility, outlet, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss GROUP BY 1, 2 ORDER BY 2, 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.facility, Collectors.groupingBy(a -> a.outlet,
                Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)))));
    }

    public Map<String, List<DataElement>> refillsOutletView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT o.id, o.name outlet, d.patient_id, date, ROW_NUMBER() OVER (PARTITION BY d.patient_Id ORDER BY
                        date DESC, d.id DESC) rn FROM imp_refill d JOIN fw_organisation o ON o.id = d.organisation_id WHERE
                    date BETWEEN ? AND ?
                ) r WHERE rn = 1 AND id = ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, o.name facility FROM Data d JOIN imp_patient p ON d.patient_Id = p.id
            	JOIN fw_organisation o ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet FROM Appointment a)
            SELECT SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)));
    }

    public Map<String, Map<String, List<DataElement>>> refillsFacilityView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT f.id, o.name outlet, f.name facility, d.patient_id, date, ROW_NUMBER() OVER
                        (PARTITION BY d.patient_Id ORDER BY date DESC, d.id DESC) rn FROM imp_refill d JOIN fw_organisation o
                        ON o.id = d.organisation_id JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id =
                    p.organisation_id WHERE date BETWEEN ? AND ?
                ) r WHERE rn = 1 AND id = ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet FROM Data d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o ON
            		o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet FROM Appointment a)
            SELECT outlet, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss GROUP BY 1 ORDER BY 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.outlet, Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup))));
    }

    public Map<String, Map<String, Map<String, List<DataElement>>>> refillsCountryView(LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT o.name outlet, f.name facility, d.patient_id, date, ROW_NUMBER() OVER
                        (PARTITION BY d.patient_Id ORDER BY date DESC, d.id DESC) rn FROM imp_refill d JOIN fw_organisation o ON o.id =
                        d.organisation_id JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id =
                    p.organisation_id WHERE date BETWEEN ? AND ?
                ) r WHERE rn = 1
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, facility FROM Data d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o ON
            		o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, facility, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss GROUP BY 1, 2 ORDER BY 2, 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class), start, end);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.facility, Collectors.groupingBy(r -> r.outlet,
                Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)))));
    }

    public Map<String, List<DataElement>> missedAppointmentsOutletView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT o.id, o.name outlet, d.patient_id, date_Next_Refill date, ROW_NUMBER() OVER
                    (PARTITION BY d.patient_Id ORDER BY date DESC, d.id DESC) rn FROM imp_refill d JOIN fw_organisation o
                    ON o.id = d.organisation_id WHERE date <= ?
                ) r WHERE rn = 1 AND id = ? AND date BETWEEN ? AND ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		 outlet FROM Data d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet FROM Appointment a)
            SELECT SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , end, orgId, start, end);
        return result.stream()
            .collect(Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)));
    }

    public Map<String, Map<String, List<DataElement>>> missedAppointmentsFacilityView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT f.id, o.name outlet, f.name facility, d.patient_id, date_Next_Refill date, ROW_NUMBER() OVER
                        (PARTITION BY d.patient_Id ORDER BY date DESC, d.id DESC) rn FROM imp_refill d JOIN fw_organisation o ON o.id =
                        d.organisation_id JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id =
                    p.organisation_id WHERE date <= ?
                ) r WHERE id = ? AND rn = 1 AND date BETWEEN ? AND ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, facility FROM Data d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o ON
            		o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            GROUP BY 1 ORDER BY 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , end, orgId, start, end);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.outlet, Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup))));
    }

    public Map<String, Map<String, Map<String, List<DataElement>>>> missedAppointmentsCountryView(LocalDate start, LocalDate end) {
        String query = """
            WITH Data AS (
                SELECT * FROM (
                    SELECT o.name outlet, f.name facility, d.patient_id, date_Next_Refill date, ROW_NUMBER() OVER
                        (PARTITION BY d.patient_Id ORDER BY date DESC, d.id DESC) rn FROM imp_refill d JOIN fw_organisation o ON o.id =
                        d.organisation_id JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id =
                    p.organisation_id WHERE date <= ?
                ) r WHERE rn = 1 AND date BETWEEN ? AND ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, facility FROM Data d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o ON
            		o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, facility, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            GROUP BY 1, 2 ORDER BY 2, 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , end, start, end);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.facility, Collectors.groupingBy(r -> r.outlet,
                Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)))));
    }

    public Map<String, List<DataElement>> devolveOutletView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH CurrentDevolve AS (
            	SELECT * FROM (
            		SELECT o.id. date, d.patient_id, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_devolve d JOIN fw_organisation o ON o.id = d.organisation_id
            		WHERE (reason_discontinued IS NULL OR reason_discontinued = '') AND date(date) BETWEEN ? AND ?
            	) d WHERE rn = 1 AND id = ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet FROM CurrentDevolve d JOIN imp_patient p ON d.patient_Id = p.id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet FROM Appointment a)
            SELECT SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)));
    }

    public Map<String, Map<String, List<DataElement>>> devolveFacilityView(UUID orgId, LocalDate start, LocalDate end) {
        String query = """
            WITH CurrentDevolve AS (
            	SELECT * FROM (
            		SELECT f.id, date, d.patient_id, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_devolve d JOIN fw_organisation o ON o.id = d.organisation_id
                        JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id = p.organisation_id
            		WHERE (reason_discontinued IS NULL OR reason_discontinued = '') AND date(date) BETWEEN ? AND ?
            	) d WHERE rn = 1 AND id = ?
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, o.name facility FROM CurrentDevolve d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o
            		ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            GROUP BY 1 ORDER BY 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class)
            , start, end, orgId);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.outlet, Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup))));
    }

    public Map<String, Map<String, Map<String, List<DataElement>>>> devolveCountryView(LocalDate start, LocalDate end) {
        String query = """
            WITH CurrentDevolve AS (
            	SELECT * FROM (
            		SELECT date, d.patient_id, o.name outlet, ROW_NUMBER() OVER (PARTITION BY d.patient_Id
                        ORDER BY date DESC, d.id DESC) rn FROM imp_devolve d JOIN fw_organisation o ON o.id = d.organisation_id
                        JOIN imp_patient p ON p.id = d.patient_id JOIN fw_organisation f ON f.id = p.organisation_id
            		WHERE (reason_discontinued IS NULL OR reason_discontinued = '') AND date(date) BETWEEN ? AND ?
            	) d WHERE rn = 1
            ),
            Appointment AS (
            	SELECT sex, CASE WHEN AGE(date, date_of_birth) <  INTERVAL '15 years' THEN 'u' ELSE 'o' END age,
            		outlet, o.name facility FROM CurrentDevolve d JOIN imp_patient p ON d.patient_Id = p.id JOIN fw_organisation o
            		ON o.id = p.organisation_id
            ),
            DISS AS (
                SELECT CASE WHEN a.age = 'u' AND sex = 'female' THEN 1 ELSE 0 END fu,
                    CASE WHEN a.age = 'u' AND sex = 'male' THEN 1 ELSE 0 END mu,
                    CASE WHEN a.age != 'u' THEN 1 ELSE 0 END ot,
                    outlet, facility FROM Appointment a)
            SELECT outlet, facility, SUM(fu) fu, SUM(mu) mu, SUM(ot) ot FROM diss
            GROUP BY 1, 2 ORDER BY 2, 1
            """;
        var result = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(DataElement.class), start, end);
        return result.stream()
            .collect(Collectors.groupingBy(r -> r.facility, Collectors.groupingBy(r -> r.outlet,
                Collectors.groupingBy(r -> StringUtils.trimToEmpty(r.targetGroup)))));
    }

    public void buildFacilities(Worksheet sheet,
                                int rowOffset,
                                int columnOffset,
                                Collection<String> facilities,
                                Collection<String> outlets,
                                Collection<String> targetGroups,
                                Map<String, Object> data) {

        for (String facility : facilities) {
            sheet.getCells().insertRow(rowOffset);
            Row row = sheet.getCells().getRows().get(rowOffset);
            Cell cell = row.get(columnOffset);
            cell.setValue(facility);
            Style style = cell.getStyle();
            style.getFont().setBold(true);
            style.getFont().setSize(13);
            cell.setStyle(style);
            int offset = buildOutlets(sheet, row.getIndex() + 1, cell.getColumn() + 1, facility, outlets, targetGroups, data) + 1;

            sheet.getCells().merge(rowOffset, columnOffset, 1,
                sheet.getCells().getMaxColumn());
            rowOffset = offset;
        }
    }

    public int buildOutlets(Worksheet sheet,
                            int rowOffset,
                            int columnOffset,
                            String facility,
                            Collection<String> outlets,
                            Collection<String> targetGroups,
                            Map<String, Object> data) {
        for (String outlet : outlets) {
            sheet.getCells().insertRow(rowOffset);
            Row row = sheet.getCells().getRows().get(rowOffset);
            Cell cell = row.get(columnOffset);
            Style style = cell.getStyle();
            style.getFont().setBold(true);
            style.getFont().setSize(11);
            cell.setStyle(style);
            cell.setValue(outlet);
            int offset = buildDisaggregations(sheet, row.getIndex() + 1, cell.getColumn() + 1, facility, outlet, targetGroups, data) + 1;

            sheet.getCells().merge(rowOffset, columnOffset, 1,
                sheet.getCells().getMaxColumn() - 1);
            rowOffset = offset;
        }


        return rowOffset;
    }

    @SneakyThrows
    private int buildDisaggregations(Worksheet sheet,
                                     int rowOffset,
                                     int columnOffset,
                                     String facility,
                                     String outlet,
                                     Collection<String> targetGroups,
                                     Map<String, Object> data) {
        int headerRow = rowOffset;
        sheet.getCells().insertRow(rowOffset);
        sheet.getCells().insertRow(++rowOffset);
        sheet.getCells().insertRow(++rowOffset);
        Row row = sheet.getCells().getRows().get(rowOffset);
        Cell cell = row.get(columnOffset);
        cell.setValue("Number of clients who defaulted their ART pill pick visit this month");
        sheet.getCells().insertRow(++rowOffset);
        row = sheet.getCells().getRows().get(rowOffset);
        cell = row.get(columnOffset);
        cell.setValue("Number of clients with appointments");
        sheet.getCells().insertRow(++rowOffset);
        row = sheet.getCells().getRows().get(rowOffset);
        cell = row.get(columnOffset);
        cell.setValue("Number of clients who received ARVs");
        sheet.getCells().insertRow(++rowOffset);
        row = sheet.getCells().getRows().get(rowOffset);
        cell = row.get(columnOffset);
        cell.setValue("Number of clients devolved to OFCAD sites using the Impilo App");
        sheet.getCells().insertRow(++rowOffset);
        row = sheet.getCells().getRows().get(rowOffset);
        cell = row.get(columnOffset);
        cell.setValue("Number of clients opting out of the OFCAD model by health facility");

        int headerColumn = columnOffset + 1;
        for (String targetGroup : targetGroups) {
            row = sheet.getCells().getRows().get(headerRow);
            cell = row.get(headerColumn);
            cell.setValue(targetGroup);
            Style style = cell.getStyle();
            style.getFont().setBold(true);
            style.getFont().setSize(10);
            style.setHorizontalAlignment(TextAlignmentType.CENTER);
            cell.setStyle(style);
            row = sheet.getCells().getRows().get(headerRow + 1);
            cell = row.get(headerColumn);
            cell.setValue("Female <15");
            cell = row.get(headerColumn + 1);
            cell.setValue("Male <15");
            cell = row.get(headerColumn + 2);
            cell.setValue("Total >15");
            sheet.getCells().merge(headerRow, headerColumn, 1, 3);
            List<String> reportTypes = List.of("missedAppointments", "appointments", "refills", "devolves", "discontinues");
            for (String type : reportTypes) {
                Object dataElement = data.get(type);
                long fu = 0;
                long mu = 0;
                long ot = 0;
                if (dataElement != null) {
                    if (facility != null && outlet != null) {
                        fu = getValue((Map<String, Map<String, Map<String, List<DataElement>>>>) dataElement, facility, outlet, targetGroup, "fu");
                        mu = getValue((Map<String, Map<String, Map<String, List<DataElement>>>>) dataElement, facility, outlet, targetGroup, "mu");
                        ot = getValue((Map<String, Map<String, Map<String, List<DataElement>>>>) dataElement, facility, outlet, targetGroup, "ot");
                    } else if (outlet != null) {
                        fu = getValue((Map<String, Map<String, List<DataElement>>>) dataElement, outlet, targetGroup, "fu");
                        mu = getValue((Map<String, Map<String, List<DataElement>>>) dataElement, outlet, targetGroup, "mu");
                        ot = getValue((Map<String, Map<String, List<DataElement>>>) dataElement, outlet, targetGroup, "ot");
                    } else {
                        fu = getValue((Map<String, List<DataElement>>) dataElement, targetGroup, "fu");
                        mu = getValue((Map<String, List<DataElement>>) dataElement, targetGroup, "mu");
                        ot = getValue((Map<String, List<DataElement>>) dataElement, targetGroup, "ot");
                    }
                }

                row = sheet.getCells().getRows().get(row.getIndex() + 1);
                Cell dc = row.get(headerColumn);
                dc.setValue(fu);
                dc = row.get(headerColumn + 1);
                dc.setValue(mu);
                dc = row.get(headerColumn + 2);
                dc.setValue(ot);
            }

            headerColumn += 3;
        }


        return rowOffset;
    }

    @Getter
    @Setter
    @ToString
    public static class DataElement {
        public String outlet;
        public String facility;
        public Long fu;
        public Long mu;
        public Long ot;
        private String targetGroup;

        public void setTargetGroup(String targetGroup) {
            this.targetGroup = StringUtils.trimToEmpty(targetGroup);
        }

        public void setOutlet(String outlet) {
            this.outlet = StringUtils.trimToEmpty(outlet);
        }
    }
}
