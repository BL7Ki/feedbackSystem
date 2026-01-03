package com.techChallenge.feedbackSystem.dto.feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequestDTO {
    @JsonProperty("descricao")
    private String description;

    @JsonProperty("nota")
    private int rating;
}