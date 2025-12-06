package com.techChallenge.feedbackSystem.domain.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyReport {
    private String description;
    private boolean urgent;
    private LocalDateTime sentAt;
    private long evaluationsPerDay;
    private long evaluationsPerUrgency;
}
