package com.techChallenge.feedbackSystem.domain.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WeeklyReport {
    private String reportKey;
    private String period;
    private double averageRating;
    private long totalFeedbacks;
    private String createdAt;
}