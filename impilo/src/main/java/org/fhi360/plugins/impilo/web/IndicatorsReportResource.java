package org.fhi360.plugins.impilo.web;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.fhi360.plugins.impilo.services.IndicatorsReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/impilo/reporting/indicators")
@RequiredArgsConstructor
public class IndicatorsReportResource {
    private final IndicatorsReportService indicatorsReportService;

    @GetMapping
    public void getReport(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream out = indicatorsReportService.generateReport(startDate, endDate);
        setStream(out, response);
    }

    private void setStream(ByteArrayOutputStream baos, HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Length", Integer.valueOf(baos.size()).toString());
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(baos.toByteArray());
        outputStream.close();
        response.flushBuffer();
    }
}
