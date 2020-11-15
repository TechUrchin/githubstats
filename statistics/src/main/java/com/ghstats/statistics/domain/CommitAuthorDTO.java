package com.ghstats.statistics.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommitAuthorDTO {
    private String gitName;
    private String gitEmail;
    private String githubUsername;
    private String githubUserIcon;
}
