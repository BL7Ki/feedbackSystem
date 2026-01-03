package com.techChallenge.feedbackSystem.service.feedback;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.dto.feedback.FeedbackRequestDTO;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Instant;
import java.util.UUID;

@Service
public class FeedbackService {

    private final FeedbackRepository repository;
    private final SqsClient sqsClient;

    @Value("${aws.sqs.queueUrl}")
    private String queueUrl;

    public FeedbackService(FeedbackRepository repository, SqsClient sqsClient) {
        this.repository = repository;
        this.sqsClient = sqsClient;
    }

    public String processIngestion(FeedbackRequestDTO request) {
        Feedback feedback = mapToFeedback(request);
        Feedback savedFeedback = repository.save(feedback);
        sendToSqs(savedFeedback);

        return savedFeedback.getId();
    }

    private Feedback mapToFeedback(FeedbackRequestDTO request) {
        Feedback feedback = new Feedback();
        feedback.setId(UUID.randomUUID().toString());
        feedback.setDescription(request.getDescription());
        feedback.setRating(request.getRating());

        if (request.getRating() <= 3) {
            feedback.setUrgency("CRITICO");
        } else {
            feedback.setUrgency("NORMAL");
        }

        feedback.setCreatedAt(Instant.now().toString());

        return feedback;
    }

    private void sendToSqs(Feedback savedFeedback) {
        String messageBody = String.format("{\"feedbackId\": \"%s\"}", savedFeedback.getId());

        sqsClient.sendMessage(builder -> builder
                .queueUrl(queueUrl)
                .messageBody(messageBody)
        );

        System.out.println("Mensagem enviada para SQS: " + savedFeedback.getId());
    }
}