package com.ghstats.reports.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ReportService {

    private static final float startX = 70;
    private static final float startY = 700;

    private static final PDFont DEFAULT_FONT = PDType1Font.COURIER;

    private PDDocument document;
    private PDPage page;
    private PDPageContentStream contentStream;

    public void generatePdf(String location, String repoName) throws IOException {
        document = new PDDocument();
        page = new PDPage();
        contentStream = new PDPageContentStream(document, page);

        document.addPage(page);

        contentStream.beginText();
        writeHeader("Statistics for GitHub repository " + repoName);
        writeRepoStatistics();
        writeContributorStatistics();

        contentStream.endText();
        contentStream.close();
        document.save(location);
        document.close();
    }

    private void writeNewLineAtCoordinates(PDFont font, int fontSize, String content, float x, float y) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(x, y);
        contentStream.showText(content);
    }

    private void writeNewLine(PDFont font, int fontSize, String content) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.newLine();
        contentStream.showText(content);
    }

    private void writeHeader(String content) throws IOException {
        contentStream.setLeading(14.5f);
        writeNewLineAtCoordinates(PDType1Font.COURIER_BOLD, 18, content, startX, startY);
    }

    private void writeRepoStatistics() throws IOException {
        String header = "Repository statistics";
        writeNewLine(DEFAULT_FONT, 16, header);
        contentStream.newLine();
        contentStream.setFont(DEFAULT_FONT, 12);
        contentStream.showText("Total commits: " + 10);
        contentStream.newLine();
        contentStream.showText("First commit: " + LocalDate.now().toString());
        contentStream.newLine();
        contentStream.showText("Latest commit: " + LocalDate.now().toString());
        contentStream.newLine();
        contentStream.showText("Activity duration: " + 10 + " days");
        contentStream.newLine();
    }

    private void writeContributorStatistics() throws IOException {
        String header = "Contributor statistics";
        writeNewLine(DEFAULT_FONT, 16, header);
        contentStream.newLine();
        contentStream.setFont(DEFAULT_FONT, 14);
        contentStream.showText("1. iigne ");
        contentStream.newLine();

        contentStream.showText("2. shimmamconyx");
        contentStream.newLine();
    }

}
