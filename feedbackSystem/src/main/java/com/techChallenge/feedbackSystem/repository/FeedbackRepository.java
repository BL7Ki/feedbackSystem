package com.techChallenge.feedbackSystem.repository;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class FeedbackRepository {

    private final DynamoDbTable<Feedback> feedbackTable;

    public FeedbackRepository(DynamoDbEnhancedClient enhancedClient) {
        // 'FeedbackTable' é o nome da tabela no AWS SAM/Terraform
        this.feedbackTable = enhancedClient.table("FeedbackTable", TableSchema.fromBean(Feedback.class));
    }

    public Feedback save(Feedback feedback) {
        feedbackTable.putItem(feedback);
        return feedback;
    }

    // Iremos precisar de um método para buscar dados para o Lambda 3 (Relatórios)
    // Ex: Listar todos os feedbacks de uma data range
    public List<Feedback> findByDateRange(LocalDateTime start, LocalDateTime end) {
        // Implementar consulta usando Query/Scan ou GSI do DynamoDB
        return List.of();
    }
}