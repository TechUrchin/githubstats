package com.ghstats.reports.controller;

import com.ghstats.reports.domain.StatisticsDTO;
import com.ghstats.reports.service.ReportGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/api/reports")
public class ReportController {

    private static final String REPORT_LOCATION = "";
    private static final String REPORT_FILENAME = "%s-%s.pdf";

    @PostMapping(path = "/pdf")
    private ResponseEntity<ByteArrayResource> getReportLink(@RequestBody StatisticsDTO statistics) {
        String repoName = statistics.getRepoStats().getRepoName();
        String filename = String.format(REPORT_FILENAME, repoName, LocalDateTime.now().toString());
        String link = REPORT_LOCATION + filename;
        try {
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generatePdf(link, statistics);

            File file = new File(filename);
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
            return ResponseEntity.ok()
                    .headers(headers(filename))
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private HttpHeaders headers(String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" +filename);
        return headers;
    }
}
