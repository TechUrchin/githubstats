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
public class ContributorDTO {
    private String gitName;
    private String gitEmail;
    private String gitUsername;
    private String gitUserIcon;
    private int numberOfCommits;
    private LocalDateTime firstCommit;
    private LocalDateTime lastCommit;
}
