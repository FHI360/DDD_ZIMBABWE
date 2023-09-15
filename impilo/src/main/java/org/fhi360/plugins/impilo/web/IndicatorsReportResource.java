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

/**
 * The IndicatorsReportResource class is a REST controller that generates and returns a report in the form of an
 * octet-stream.
 */
@RestController
@RequestMapping("/api/impilo/reporting/indicators")
@RequiredArgsConstructor
public class IndicatorsReportResource {
    private final IndicatorsReportService indicatorsReportService;

    /**
     * The function `getReport` generates a report using the `indicatorsReportService` based on the provided start and end
     * dates, and sets the generated report as the response stream.
     *
     * @param startDate The `startDate` parameter is of type `LocalDate` and represents the starting date for the report.
     * It is annotated with `@RequestParam`, which means it is expected to be passed as a query parameter in the request
     * URL.
     * @param endDate The `endDate` parameter is of type `LocalDate` and represents the end date of the report.
     * @param response The `response` parameter is an instance of the `HttpServletResponse` class, which represents the
     * response that will be sent back to the client. It is used to set the response headers and write the response
     * content. In this case, it is being used to set the response content to the generated report
     */
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
