package com.techChallenge.feedbackSystem.repository;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class FeedbackRepositoryTest {
    private DynamoDbEnhancedClient mockClient;
    private DynamoDbTable<Feedback> mockTable;
    private FeedbackRepository repository;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        mockClient = Mockito.mock(DynamoDbEnhancedClient.class);
        mockTable = Mockito.mock(DynamoDbTable.class);
        Mockito.when(mockClient.table(any(), any(TableSchema.class))).thenReturn(mockTable);
        repository = new FeedbackRepository(mockClient);
    }

    @Test
    void testSave() {
        Feedback feedback = new Feedback();
        Mockito.doNothing().when(mockTable).putItem(feedback);
        Feedback result = repository.save(feedback);
        assertEquals(feedback, result);
        Mockito.verify(mockTable).putItem(feedback);
    }

    @Test
    void testFindByIdNotFound() {
        Mockito.when(mockTable.getItem(Mockito.any(Key.class))).thenReturn(null);
        Optional<Feedback> result = repository.findById("id2");
        assertFalse(result.isPresent());
    }
    

}
