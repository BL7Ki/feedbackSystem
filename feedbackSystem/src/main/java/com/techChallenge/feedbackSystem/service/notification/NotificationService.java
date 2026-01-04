package com.techChallenge.feedbackSystem.service.notification;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.dto.notification.FeedbackMessageDTO;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;

import java.time.Instant;

@Service
public class NotificationService {

    private final FeedbackRepository repository;
    private final SnsClient snsClient;

    @Value("${AWS_SNS_CRITICALTOPICARN:}")
    private String criticalTopicArn;

    public NotificationService(FeedbackRepository repository, SnsClient snsClient) {
        this.repository = repository;
        this.snsClient = snsClient;
    }

    public void processMessage(FeedbackMessageDTO message) {
        Feedback feedback = repository.findById(message.getFeedbackId())
                .orElseThrow(() -> new RuntimeException("Feedback não encontrado: " + message.getFeedbackId()));

        if ("CRITICO".equals(feedback.getUrgency()) || feedback.getRating() <= 3) {

            String alertMessage = String.format(
                    "ALERTA DE FEEDBACK CRÍTICO\n" +
                            "--------------------------\n" +
                            "ID: %s\n" +
                            "Descrição: %s\n" +
                            "Nota: %d\n" +
                            "Data: %s",
                    feedback.getId(),
                    feedback.getDescription(),
                    feedback.getRating(),
                    feedback.getCreatedAt());

            snsClient.publish(builder -> builder
                    .topicArn(criticalTopicArn)
                    .subject("Urgência: Feedback Crítico Recebido")
                    .message(alertMessage)
            );

            System.out.println("Notificação crítica enviada para o tópico SNS: " + feedback.getId());
        }
    }
}