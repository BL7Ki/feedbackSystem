package com.techChallenge.feedbackSystem.service.feedback;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.dto.feedback.FeedbackRequestDTO;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

@Service
public class FeedbackService {

    private final FeedbackRepository repository;
    private final SqsClient sqsClient;

    public FeedbackService(FeedbackRepository repository, SqsClient sqsClient) {
        this.repository = repository;
        this.sqsClient = sqsClient;
    }

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    public String processIngestion(FeedbackRequestDTO request) {
        Feedback feedback = mapToFeedback(request);
        Feedback savedFeedback = repository.save(feedback);
        sendToSqs(savedFeedback);

        return savedFeedback.getId();
    }

    private Feedback mapToFeedback(FeedbackRequestDTO request) {
        Feedback feedback = new Feedback();
        feedback.setDescription(request.getDescription());
        feedback.setRating(request.getRating());
        return feedback;
    }

    private void sendToSqs(Feedback savedFeedback) {
        String messageBody = String.format("{\"feedbackId\": \"%s\"}", savedFeedback.getId());

        sqsClient.sendMessage(builder -> builder
                .queueUrl(queueUrl)
                .messageBody(messageBody)
        );
    }
}