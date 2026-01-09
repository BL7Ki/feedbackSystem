package com.techChallenge.feedbackSystem.function.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techChallenge.feedbackSystem.dto.notification.FeedbackMessageDTO;
import com.techChallenge.feedbackSystem.service.notification.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;

import java.util.function.Consumer;

@Component
public class NotificationProcessor {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public NotificationProcessor(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public Consumer<SQSEvent> processNotifications() {
        return event -> {
            if (event.getRecords() == null) return;

            for (SQSMessage sqsMessage : event.getRecords()) {
                try {
                    System.out.println("Processando mensagem ID: " + sqsMessage.getMessageId());

                    String body = sqsMessage.getBody();
                    FeedbackMessageDTO messageDto = objectMapper.readValue(body, FeedbackMessageDTO.class);

                    notificationService.processMessage(messageDto);

                } catch (Exception e) {
                    System.err.println("Erro crítico no processamento do SQS: " + e.getMessage());
                    throw new RuntimeException("Erro ao processar registro SQS", e);
                }
            }
        };
    }
}