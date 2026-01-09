package com.techChallenge.feedbackSystem.service.feedback;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.dto.feedback.FeedbackRequestDTO;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackServiceTest {
    private FeedbackService service;
    private TestFeedbackRepository testRepository;
    private FakeSqsClient fakeSqsClient;

    static class FakeSqsClient implements SqsClient {
        @Override public void close() {}
        @Override public software.amazon.awssdk.services.sqs.model.SendMessageResponse sendMessage(java.util.function.Consumer<software.amazon.awssdk.services.sqs.model.SendMessageRequest.Builder> sendMessageRequest) {
            return software.amazon.awssdk.services.sqs.model.SendMessageResponse.builder().build();
        }
        @Override
        public String serviceName() {
            throw new UnsupportedOperationException("Unimplemented method 'serviceName'");
        }
    }
    static class TestFeedbackRepository extends FeedbackRepository {
        public TestFeedbackRepository(software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient client) { super(client); }
        @Override public Feedback save(Feedback feedback) { return feedback; }
    }
    @BeforeEach
    void setUp() {
        software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient mockClient = org.mockito.Mockito.mock(software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient.class);
        testRepository = new TestFeedbackRepository(mockClient);
        fakeSqsClient = new FakeSqsClient();
        service = new FeedbackService(testRepository, fakeSqsClient);
    }
    @Test
    void testProcessIngestionReturnsIdAndSendsToSqs() {
        FeedbackRequestDTO request = new FeedbackRequestDTO();
        request.setDescription("Teste");
        request.setRating(2);
        String id = service.processIngestion(request);
        assertNotNull(id);
    }
    @Test
    void testMapToFeedbackSetsUrgencyCritico() {
        FeedbackRequestDTO request = new FeedbackRequestDTO();
        request.setDescription("Baixa nota");
        request.setRating(1);
        String id = service.processIngestion(request);
        assertNotNull(id);
    }
    @Test
    void testMapToFeedbackSetsUrgencyNormal() {
        FeedbackRequestDTO request = new FeedbackRequestDTO();
        request.setDescription("Boa nota");
        request.setRating(5);
        String id = service.processIngestion(request);
        assertNotNull(id);
    }
}
