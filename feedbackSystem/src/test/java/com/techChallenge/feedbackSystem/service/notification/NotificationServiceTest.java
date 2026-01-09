package com.techChallenge.feedbackSystem.service.notification;

import com.techChallenge.feedbackSystem.dto.notification.FeedbackMessageDTO;
import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sns.SnsClient;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {
    private NotificationService service;
    private TestFeedbackRepository testRepository;
    private FakeSnsClient fakeSnsClient;

    static class FakeSnsClient implements SnsClient {
        @Override public void close() {}
        @Override public software.amazon.awssdk.services.sns.model.PublishResponse publish(java.util.function.Consumer<software.amazon.awssdk.services.sns.model.PublishRequest.Builder> publishRequest) {
            return software.amazon.awssdk.services.sns.model.PublishResponse.builder().build();
        }
        @Override
        public String serviceName() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'serviceName'");
        }
    }
    static class TestFeedbackRepository extends FeedbackRepository {
        public TestFeedbackRepository(software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient client) { super(client); }
        @Override public java.util.Optional<Feedback> findById(String id) {
            Feedback feedback = new Feedback();
            feedback.setId(id);
            feedback.setDescription("Teste");
            feedback.setRating(1);
            feedback.setUrgency("CRITICO");
            feedback.setCreatedAt("2024-01-01T00:00:00Z");
            return java.util.Optional.of(feedback);
        }
    }
    @BeforeEach
    void setUp() {
        software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient mockClient = org.mockito.Mockito.mock(software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient.class);
        testRepository = new TestFeedbackRepository(mockClient);
        fakeSnsClient = new FakeSnsClient();
        service = new NotificationService(testRepository, fakeSnsClient);
    }
    @Test
    void testProcessMessageWithCriticalFeedback() {
        FeedbackMessageDTO message = new FeedbackMessageDTO();
        message.setFeedbackId("test-id");
        assertDoesNotThrow(() -> service.processMessage(message));
    }
    @Test
    void testProcessMessageWithNullMessage() {
        assertDoesNotThrow(() -> service.processMessage(null));
    }
    @Test
    void testProcessMessageWithEmptyId() {
        FeedbackMessageDTO message = new FeedbackMessageDTO();
        message.setFeedbackId("");
        assertDoesNotThrow(() -> service.processMessage(message));
    }
}
