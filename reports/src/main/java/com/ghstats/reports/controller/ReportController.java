package com.ghstats.reports.controller;

import com.ghstats.reports.domain.StatisticsDTO;
import com.ghstats.reports.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/api/reports")
public class ReportController {

    private static final String reportLink = "reports/src/main/resources/pdf/%s-%s.pdf";

    private final ReportService reportService;

    @PostMapping(path = "/pdf")
    private ResponseEntity<String> getReportLink(@RequestBody StatisticsDTO statistics) {
        String repoName = statistics.getRepoStats().getRepoName();
        String link = String.format(reportLink, repoName, LocalDateTime.now().toString());
        try {
            reportService.generatePdf(link, statistics);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
        return ResponseEntity.ok(link);
    }
}
