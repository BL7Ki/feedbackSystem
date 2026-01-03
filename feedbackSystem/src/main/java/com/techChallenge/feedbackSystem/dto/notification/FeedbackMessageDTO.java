package com.techChallenge.feedbackSystem.dto.notification;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackMessageDTO {
    @JsonProperty("idFeedback")
    private String feedbackId;
}