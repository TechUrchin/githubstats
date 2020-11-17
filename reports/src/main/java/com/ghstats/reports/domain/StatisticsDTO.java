package com.ghstats.reports.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDTO {
    private RepoStatsDTO repoStats;
    private List<ContributorDTO> contributorStats;
}
