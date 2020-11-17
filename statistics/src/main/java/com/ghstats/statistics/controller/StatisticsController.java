package com.ghstats.statistics.controller;

import com.ghstats.statistics.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/api/stats")

public class StatisticsController {

    @PostMapping(path= "/generate")
    private ResponseEntity<StatisticsDTO> getStats(@RequestBody RepositoryCommitsDTO repository) {

        RepoStatsDTO repoStats = generateRepoStats(repository);
        List<ContributorDTO> contributorStats = generateContributorStats(repository);

        StatisticsDTO stats = new StatisticsDTO(repoStats, contributorStats);

        return ResponseEntity.ok(stats);
    }

    public RepoStatsDTO generateRepoStats(RepositoryCommitsDTO repository) {
        String repoName = repository.getRepositoryName();
        String repoOwner = repository.getRepositoryName();
        List<CommitDTO> listOfCommits = repository.getCommits();

        int totalCommits = listOfCommits.size();
        LocalDateTime firstCommit = listOfCommits.stream().map(c -> c.getCommitDate()).min(LocalDateTime::compareTo).get();
        LocalDateTime lastCommit = listOfCommits.stream().map(c -> c.getCommitDate()).max(LocalDateTime::compareTo).get();
        //count number of unique days there's been activity in the repository
        int daysActive = (int) listOfCommits.stream().map(c -> c.getCommitDate().toLocalDate()).distinct().count();

        RepoStatsDTO repoStats = new RepoStatsDTO();
        repoStats.setRepoName(repoName);
        repoStats.setRepoOwner(repoOwner);
        repoStats.setNumberOfCommits(totalCommits);
        repoStats.setFirstCommit(firstCommit);
        repoStats.setLastCommit(lastCommit);
        repoStats.setDaysActive(daysActive);


        return repoStats;
    }

    public List<ContributorDTO> generateContributorStats(RepositoryCommitsDTO repository) {

        List<CommitDTO> listOfCommits = repository.getCommits();
        List<ContributorDTO> listOfContributors = new ArrayList<>();

        for (CommitDTO commit : listOfCommits) {

            String gitName = commit.getAuthor().getGitName();
            String gitEmail = commit.getAuthor().getGitEmail();
            String githubUsername = commit.getAuthor().getGithubUsername();
            String githubUserIcon = commit.getAuthor().getGithubUserIcon();

            if (listOfContributors.stream().noneMatch(c->c.getGitEmail().equals(gitEmail))) {
                ContributorDTO contributor = new ContributorDTO();
                contributor.setGitName(gitName);
                contributor.setGitEmail(gitEmail);
                contributor.setGitUsername(githubUsername);
                contributor.setGitUserIcon(githubUserIcon);
                contributor.setFirstCommit(listOfCommits.stream().filter(c->c.getAuthor().getGitEmail().equals(gitEmail)).map(CommitDTO::getCommitDate).min(LocalDateTime::compareTo).get());
                contributor.setLastCommit(listOfCommits.stream().filter(c->c.getAuthor().getGitEmail().equals(gitEmail)).map(CommitDTO::getCommitDate).max(LocalDateTime::compareTo).get());
                contributor.setNumberOfCommits((int) listOfCommits.stream().filter(c -> c.getAuthor().getGitEmail().equals(gitEmail)).count());
                contributor.setDaysActive((int) listOfCommits.stream().filter(c->c.getAuthor().getGitEmail().equals(gitEmail)).map(c -> c.getCommitDate().toLocalDate()).distinct().count());

                listOfContributors.add(contributor);
            }
        }

        return listOfContributors.stream().sorted(Comparator.comparingInt(ContributorDTO::getNumberOfCommits).reversed())
                .collect(Collectors.toList());
    }
}