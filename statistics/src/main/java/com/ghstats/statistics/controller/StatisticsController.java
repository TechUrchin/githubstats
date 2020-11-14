package com.ghstats.statistics.controller;

import com.ghstats.statistics.domain.CommitDTO;
import com.ghstats.statistics.domain.ContributorDTO;
import com.ghstats.statistics.domain.RepositoryCommitsDTO;
import com.ghstats.statistics.domain.StatisticsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping(path = "/api/stats")

public class StatisticsController {

    @PostMapping(path= "/generate")
    private ResponseEntity<StatisticsDTO> getStats(@RequestBody RepositoryCommitsDTO repository) {

        String potato = generateStats(repository);


        return ResponseEntity.ok().build();
    }

    public String generateStats(RepositoryCommitsDTO repository) {
        String repoName = repository.getRepositoryName();
        String repoOwner = repository.getRepositoryName();
        List<CommitDTO> listOfCommits = repository.getCommits();

        int totalCommits = listOfCommits.size();
        LocalDateTime firstCommit = listOfCommits.stream().map(c -> c.getCommitDate()).min(LocalDateTime::compareTo).get();
        LocalDateTime lastCommit = listOfCommits.stream().map(c -> c.getCommitDate()).max(LocalDateTime::compareTo).get();

        List<String> listOfGitNames = new ArrayList<>();
        List<String> listOfGitEmails = new ArrayList<>();
        List<ContributorDTO> listOfContributors = new ArrayList<>();

        for (CommitDTO commit : listOfCommits) {

            String gitName = commit.getAuthor().getGitName();
            String gitEmail = commit.getAuthor().getGitEmail();
            String githubUsername = commit.getAuthor().getGithubUsername();
            String githubUserIcon = commit.getAuthor().getGithubUserIcon();

            if (!listOfContributors.contains(gitName)){
                ContributorDTO contributor = new ContributorDTO();
                contributor.setGitName(gitName);
                contributor.setGitEmail(gitEmail);
                contributor.setGitUsername(githubUsername);
                contributor.setGitUserIcon(githubUserIcon);
                contributor.setFirstCommit(listOfCommits.stream().filter(c->c.getAuthor().getGitName().equals(gitName)).map(c -> c.getCommitDate()).min(LocalDateTime::compareTo).get());
                contributor.setLastCommit(listOfCommits.stream().filter(c->c.getAuthor().getGitName().equals(gitName)).map(c -> c.getCommitDate()).max(LocalDateTime::compareTo).get());
                contributor.setNumberOfCommits((int) listOfCommits.stream().filter(c -> c.getAuthor().getGitName().equals(gitName)).count());

                listOfContributors.add(contributor);
            }

        }



        return stats;
    }
}