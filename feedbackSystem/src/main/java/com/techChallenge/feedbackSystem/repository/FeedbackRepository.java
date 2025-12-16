package com.techChallenge.feedbackSystem.repository;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.enhanced.dynamodb.Expression;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

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

    /**
     * Busca feedbacks em um intervalo de datas usando o GSI (Scan com filtro indexado).
     */
    public List<Feedback> findByDateRange(LocalDateTime start, LocalDateTime end) {
        String indexName = "CreatedAtIndex";

        DateTimeFormatter fmt = DateTimeFormatter.ISO_DATE_TIME;
        String startString = start.format(fmt);
        String endString = end.format(fmt);

        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":start", AttributeValue.builder().s(startString).build());
        expressionValues.put(":end", AttributeValue.builder().s(endString).build());

        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#createdAt", "createdAt");

        Expression filterExpression = Expression.builder()
                .expression("#createdAt BETWEEN :start AND :end")
                .expressionNames(expressionNames)
                .expressionValues(expressionValues)
                .build();

        return feedbackTable.index(indexName)
                .scan(r -> r.filterExpression(filterExpression))
                .stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }
}