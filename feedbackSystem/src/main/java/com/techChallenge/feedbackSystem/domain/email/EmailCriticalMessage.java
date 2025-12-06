package com.techChallenge.feedbackSystem.domain.email;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmailCriticalMessage {
    private String description;
    private boolean urgent;
    private LocalDateTime sentAt;
}
