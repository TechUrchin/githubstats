package com.ghstats.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepoStatsDTO {
    private String repoName;
    private String repoOwner;
    private int numberOfCommits;
    private LocalDateTime firstCommit;
    private LocalDateTime lastCommit;
}
