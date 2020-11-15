package com.ghstats.reports.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class ReportService {

    private static final float startX = 70;
    private static final float FRONT_PAGE_START_Y = 650;
    private static final float PAGE_WITH_HEADER_START_Y = 750;
    private static final float FULL_PAGE_START_Y = 800;

    private static final PDFont DEFAULT_FONT = PDType1Font.COURIER;
    private static final PDFont DEFAULT_FONT_BOLD = PDType1Font.COURIER_BOLD;
    private static final PDFont HEADER_FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA;


    private static final String GH_LOGO_LOCATION = "reports/src/main/resources/img/GitHub-Mark.png";

    private PDDocument document;
    private PDPage page;
    private PDPageContentStream contentStream;

    public void generatePdf(String location, String repoName) throws IOException {
        document = new PDDocument();
        createNewCurrentPage(FRONT_PAGE_START_Y, false);

        writeHeader(repoName, "potato");
        writeRepoStatisticsSection();
        writeContributorStatisticsSection();

        closeCurrentPage();
        document.save(location);
        document.close();
    }

    private void writeHeader(String repoName, String repoOwner) throws IOException {

        addImage();
        beginText(FRONT_PAGE_START_Y, 21f);
        contentStream.setFont(HEADER_FONT_BOLD, 24);
        contentStream.showText("GitHub repository statistics report");
        contentStream.newLine();
        contentStream.setFont(HEADER_FONT, 16);

        contentStream.showText("for repository ");
        contentStream.setFont(HEADER_FONT_BOLD, 20);
        contentStream.showText(String.format("%s/%s", repoOwner, repoName));
        contentStream.newLine();

        contentStream.setFont(HEADER_FONT_BOLD, 12);
        contentStream.showText("created " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")));
        contentStream.newLine();
        writeBoldSeparator();
        contentStream.newLine();
    }

    private void addImage() throws IOException {
        PDImageXObject image = PDImageXObject.createFromFile(GH_LOGO_LOCATION, document);
        contentStream.drawImage(image, 200, 680, 150, 150);
    }

    private void writeRepoStatisticsSection() throws IOException {
        String header = "Total repository statistics";
        int totalCommits = 51655;
        contentStream.setLeading(14.5f);
        contentStream.setFont(HEADER_FONT_BOLD, 18);
        contentStream.showText(header);

        contentStream.newLine();
        contentStream.newLine();
        writeStatistics(totalCommits, LocalDate.now().minusDays(5), LocalDate.now(), 10);
        writeBoldSeparator();
    }

    private void writeContributorStatisticsSection() throws IOException {
//        createNewCurrentPage(PAGE_WITH_HEADER_START_Y, true);
        String header = "Contributor leaderboard";
        contentStream.newLine();
        contentStream.setFont(HEADER_FONT_BOLD, 18);
        contentStream.showText(header);

        List<String> users = Arrays.asList("Igne Cat", "Jay Cat", "Charlie Cat", "Spider Plant", "Chickwheat Seitan", "Buster Neighbour");
        int index = 1;
        contentStream.newLine();
        for(String user : users) {
            writeContributorStatistics(index, user, user+"username", user+"@email.com", 10, LocalDate.now().minusDays(2), LocalDate.now());
            if(isNewPageNeeded(index, users.size())) {
                createNewContributorsPage();
            }
            index ++;
        }
        contentStream.newLine();
    }

    private boolean isNewPageNeeded(int index, int lastIndex) {
        //First page can only fit 2 contributors components. Every page after that can fit 5.
        // Need to handle special case when the last element fills up the page, so new page is not created.
        return index == 2 ||
                (index%(5+2) == 0 && index!=lastIndex);
    }

    private void createNewContributorsPage() throws IOException {
        writeLightSeparator();
        createNewCurrentPage(FULL_PAGE_START_Y, true);
        contentStream.newLine();
    }


    private void writeContributorStatistics(int index, String name, String username, String email, int totalCommits, LocalDate firstCommit, LocalDate lastCommit) throws IOException {
        writeLightSeparator();
        contentStream.newLine();
        contentStream.setFont(DEFAULT_FONT_BOLD, 16);
        contentStream.showText(String.format("%d. %s ", index, name));
        contentStream.setFont(DEFAULT_FONT, 12);
        contentStream.showText(String.format("Username: %s", username.toLowerCase().replaceAll(" ", "")));
        contentStream.newLine();
        contentStream.showText(String.format("Email: %s", email.toLowerCase().replaceAll(" ", "")));
        contentStream.newLine();
        contentStream.newLine();
        writeStatistics(totalCommits, firstCommit, lastCommit, 10);
    }

    private void writeStatistics(int totalCommits, LocalDate fistCommit, LocalDate lastCommit, int activeDays) throws IOException {
        contentStream.setFont(DEFAULT_FONT, 14);
        contentStream.showText("Total commits: " + totalCommits);
        contentStream.newLine();
        contentStream.showText("First commit: " + fistCommit.toString());
        contentStream.newLine();
        contentStream.showText("Latest commit: " + lastCommit.toString());
        contentStream.newLine();
        contentStream.showText(String.format("Activity duration: %d days", activeDays));
        contentStream.newLine();
    }

    private void createNewCurrentPage(float y, boolean includeText) throws IOException {
        if(contentStream != null) closeCurrentPage();
        page = new PDPage(PDRectangle.A4);
        contentStream = new PDPageContentStream(document, page);
        document.addPage(page);
        if(includeText) {
            beginText(y, 14.5f);
        }
    }

    private void beginText(float y, float lineSpacing) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, y);
        contentStream.setLeading(lineSpacing);
        contentStream.setFont(DEFAULT_FONT, 14);
    }

    private void closeCurrentPage() throws IOException {
        contentStream.endText();
        contentStream.close();
    }

    private void writeSeparator(PDFont font) throws IOException {
        contentStream.setFont(font, 12);
        contentStream.newLine();
        contentStream.showText("__________________________________________________________________");
        contentStream.newLine();
    }

    private void writeLightSeparator() throws IOException {
        writeSeparator(DEFAULT_FONT);
    }

    private void writeBoldSeparator() throws IOException {
        writeSeparator(DEFAULT_FONT_BOLD);
    }

}
