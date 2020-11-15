package com.ghstats.reports.controller;

import com.ghstats.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportController {

//    private static final String reportLink = "reports/src/main/resources/pdf/%s-%s.pdf";
        private static final String reportLink = "reports/src/main/resources/pdf/%s.pdf";


    private final ReportService reportService;

    @PostMapping(path = "/pdf")
    private ResponseEntity<String> getReportLink() {
        String repoName = "example-repo";
        String link = String.format(reportLink, repoName);
//        String link = String.format(reportLink, repoName, LocalDateTime.now().toString());
        try {
            reportService.generatePdf(link, repoName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
        return ResponseEntity.ok(link);
    }
}
