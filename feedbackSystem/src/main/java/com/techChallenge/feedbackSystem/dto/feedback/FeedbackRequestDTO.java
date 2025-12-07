package com.techChallenge.feedbackSystem.dto.feedback;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequestDTO {
    private String description;
    private int rating;
}
