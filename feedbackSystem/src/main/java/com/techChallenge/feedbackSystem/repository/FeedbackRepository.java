package com.techChallenge.feedbackSystem.repository;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

@Repository
public class FeedbackRepository {

    private final DynamoDbTable<Feedback> feedbackTable;

    public FeedbackRepository(DynamoDbEnhancedClient enhancedClient) {
        this.feedbackTable = enhancedClient.table("FeedbackTable", TableSchema.fromBean(Feedback.class));
    }

    public Feedback save(Feedback feedback) {
        feedbackTable.putItem(feedback);
        return feedback;
    }

    public Optional<Feedback> findById(String id) {
        Key key = Key.builder().partitionValue(id).build();
        Feedback feedback = feedbackTable.getItem(r -> r.key(key));

        return Optional.ofNullable(feedback);
    }

    public Optional<Feedback> findByDateRange(String startDate, String endDate) {
        // Implementação futura para buscar feedbacks em um intervalo de datas
        return Optional.empty();
    }
}