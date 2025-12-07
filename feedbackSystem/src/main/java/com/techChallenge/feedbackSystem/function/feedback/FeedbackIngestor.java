package com.techChallenge.feedbackSystem.function.feedback;

import com.techChallenge.feedbackSystem.dto.feedback.FeedbackRequestDTO;
import com.techChallenge.feedbackSystem.service.feedback.FeedbackService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class FeedbackIngestor {

    private final FeedbackService feedbackService;

    public FeedbackIngestor(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @Bean
    public Function<FeedbackRequestDTO, String> ingestFeedback() {
        return request -> feedbackService.processIngestion(request);
    }
}