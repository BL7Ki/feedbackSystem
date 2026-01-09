package com.techChallenge.feedbackSystem.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCriticalMessage {
    private String description;
    private String urgency;
    private String sentAt;
}