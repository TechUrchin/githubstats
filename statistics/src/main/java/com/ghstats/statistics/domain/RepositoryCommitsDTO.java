package com.ghstats.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RepositoryCommitsDTO {

    private String repositoryName;
    private String repositoryOwner;
    private List<CommitDTO> commits;

}
