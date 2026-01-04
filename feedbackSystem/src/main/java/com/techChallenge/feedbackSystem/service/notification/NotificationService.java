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

    @Value("${AWS_SNS_CRITICALTOPICARN:}")
    private String criticalTopicArn;

    public NotificationService(FeedbackRepository repository, SnsClient snsClient) {
        this.repository = repository;
        this.snsClient = snsClient;
    }

    public void processMessage(FeedbackMessageDTO message) {
        try {
            if (message == null || message.getFeedbackId() == null || message.getFeedbackId().trim().isEmpty()) {
                System.err.println("AVISO: Recebida mensagem com feedbackId nulo ou vazio. Descartando mensagem.");
                return;
            }

            String feedbackId = message.getFeedbackId();
            System.out.println("Iniciando busca no DynamoDB para o ID: " + feedbackId);

            Feedback feedback = repository.findById(feedbackId).orElse(null);

            if (feedback == null) {
                System.err.println("ERRO: Feedback não encontrado no DynamoDB para o ID: " + feedbackId);
                return;
            }

            if ("CRITICO".equals(feedback.getUrgency()) || feedback.getRating() <= 3) {

                System.out.println("Nota crítica detectada (" + feedback.getRating() + "). Preparando disparo de SNS...");

                String alertMessage = String.format(
                        "ALERTA DE FEEDBACK CRÍTICO\n" +
                                "--------------------------\n" +
                                "ID: %s\n" +
                                "Descrição: %s\n" +
                                "Nota: %d\n" +
                                "Urgência: %s\n" +
                                "Data: %s",
                        feedback.getId(),
                        feedback.getDescription(),
                        feedback.getRating(),
                        feedback.getUrgency(),
                        feedback.getCreatedAt());

                snsClient.publish(builder -> builder
                        .topicArn(criticalTopicArn)
                        .subject("Urgência: Feedback Crítico Recebido")
                        .message(alertMessage)
                );

                System.out.println("Sucesso: Notificação enviada para o tópico SNS para o feedback: " + feedback.getId());
            } else {
                System.out.println("Feedback processado: Nota " + feedback.getRating() + " não exige notificação.");
            }

        } catch (Exception e) {
            System.err.println("Erro crítico ao processar notificação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}