package com.ghstats.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class CommitDTO {
    private LocalDateTime commitDate;
    private CommitAuthorDTO author;
}
