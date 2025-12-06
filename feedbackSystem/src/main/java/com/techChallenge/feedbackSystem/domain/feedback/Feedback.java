package com.techChallenge.feedbackSystem.domain.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    private Long id;
    private String description;
    private int rating;
    private LocalDateTime createdAt;
}
