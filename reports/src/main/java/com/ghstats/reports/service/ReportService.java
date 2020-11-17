package com.ghstats.reports.service;

import com.ghstats.reports.domain.ContributorDTO;
import com.ghstats.reports.domain.RepoStatsDTO;
import com.ghstats.reports.domain.StatisticsDTO;
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
    private static final float FULL_PAGE_START_Y = 800;

    private static final PDFont DEFAULT_FONT = PDType1Font.COURIER;
    private static final PDFont DEFAULT_FONT_BOLD = PDType1Font.COURIER_BOLD;
    private static final PDFont HEADER_FONT_BOLD = PDType1Font.HELVETICA_BOLD;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA;


    private static final String GH_LOGO_LOCATION = "reports/src/main/resources/img/GitHub-Mark.png";

    private PDDocument document;
    private PDPage page;
    private PDPageContentStream contentStream;

    public void generatePdf(String location, StatisticsDTO stats) throws IOException {
        document = new PDDocument();
        createNewCurrentPage(FRONT_PAGE_START_Y, false);

        writeHeader(stats.getRepoStats().getRepoName(), stats.getRepoStats().getRepoOwner());
        writeRepoStatisticsSection(stats.getRepoStats());
        writeContributorStatisticsSection(stats.getContributorStats());

        closeCurrentPage();
        document.save(location);
        document.close();
    }

    private void writeHeader(String repoName, String repoOwner) throws IOException {

        String reportCreationDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        addImage();
        beginText(FRONT_PAGE_START_Y, 21f);

        writeTextOnNewLine("GitHub repository statistics report", HEADER_FONT_BOLD, 24);
        writeTextOnNewLine("for repository ", HEADER_FONT, 16);
        writeTextOnSameLine(String.format("%s/%s", repoOwner, repoName), HEADER_FONT_BOLD, 20);
        writeTextOnNewLine("created " + reportCreationDate, HEADER_FONT_BOLD, 12);

        writeBoldSeparator();
    }

    private void addImage() throws IOException {
        PDImageXObject image = PDImageXObject.createFromFile(GH_LOGO_LOCATION, document);
        contentStream.drawImage(image, 200, 680, 150, 150);
    }

    private void writeRepoStatisticsSection(RepoStatsDTO repoStatsDTO) throws IOException {

        contentStream.setLeading(14.5f);
        writeTextOnNewLine("Total repository statistics", HEADER_FONT_BOLD, 18);
        writeBlankLines(1);
        writeStatistics(repoStatsDTO.getNumberOfCommits(), repoStatsDTO.getFirstCommit().toLocalDate(),
                repoStatsDTO.getLastCommit().toLocalDate(), repoStatsDTO.getDaysActive());

        writeBoldSeparator();
    }

    private void writeContributorStatisticsSection(List<ContributorDTO> contributors) throws IOException {

        writeTextOnNewLine("Contributor leaderboard", HEADER_FONT_BOLD, 18);

        int index = 1;
        contentStream.newLine();
        for(ContributorDTO contributor : contributors) {
            writeContributorStatistics(index, contributor);
            if(isNewPageNeeded(index, contributors.size())) {
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

    private void writeTextOnNewLine(String content) throws IOException {
        contentStream.newLine();
        contentStream.showText(content);
    }

    private void writeTextOnNewLine(String content, PDFont font, int fontSize) throws IOException {
        contentStream.setFont(font, fontSize);
        writeTextOnNewLine(content);
    }

    private void writeTextOnSameLine(String content, PDFont font, int fontSize) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.showText(content);
    }


    private void createNewContributorsPage() throws IOException {
        writeLightSeparator();
        createNewCurrentPage(FULL_PAGE_START_Y, true);
        contentStream.newLine();
    }

    private void writeContributorStatistics(int index, ContributorDTO contributor) throws IOException {

        writeLightSeparator();
        writeTextOnNewLine(String.format("%d. %s ", index, contributor.getGitName()), DEFAULT_FONT_BOLD, 16);
        writeTextOnSameLine(String.format("Username: %s", contributor.getGitUsername()), DEFAULT_FONT, 12);
        writeTextOnNewLine(String.format("Email: %s", contributor.getGitEmail()));
        writeBlankLines(1);

        writeStatistics(contributor.getNumberOfCommits(), contributor.getFirstCommit().toLocalDate(),
                contributor.getLastCommit().toLocalDate(), contributor.getDaysActive());
    }

    private void writeStatistics(int totalCommits, LocalDate fistCommit, LocalDate lastCommit, int activeDays) throws IOException {
        writeTextOnNewLine("Total commits: " + totalCommits, DEFAULT_FONT, 14);
        writeTextOnNewLine("First commit: " + fistCommit.toString());
        writeTextOnNewLine("Latest commit: " + lastCommit.toString());
        writeTextOnNewLine(String.format("Activity duration: %d days", activeDays));
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

    private void writeBlankLines(int number) throws IOException {
        for(int i=0; i<number; i++) {
            contentStream.newLine();
        }
    }

}
