package com.techChallenge.feedbackSystem.service.notification;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.dto.notification.FeedbackMessageDTO;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;

@Service
public class NotificationService {

    private final FeedbackRepository repository;
    private final SnsClient snsClient;

    public NotificationService(FeedbackRepository repository, SnsClient snsClient) {
        this.repository = repository;
        this.snsClient = snsClient;
    }

    @Value("${aws.sns.criticalTopicArn}")
    private String criticalTopicArn;

    public void processMessage(FeedbackMessageDTO message) {

        Feedback feedback = repository.findById(message.getFeedbackId())
                .orElseThrow(() -> new RuntimeException("Feedback não encontrado!"));

        if (feedback.getRating() < 5) {

            String alertMessage = String.format("Feedback Crítico Recebido!\nID: %s\nDescrição: %s\nAvaliação: %d",
                    feedback.getId(),
                    feedback.getDescription(),
                    feedback.getRating());

            snsClient.publish(builder -> builder
                    .topicArn(criticalTopicArn)
                    .message(alertMessage)
            );
        }
    }
}