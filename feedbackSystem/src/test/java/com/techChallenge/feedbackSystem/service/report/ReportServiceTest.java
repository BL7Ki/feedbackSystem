package com.techChallenge.feedbackSystem.service.report;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;

import java.util.Collections;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {
    private ReportService service;
    private TestFeedbackRepository testRepository;
    private FakeS3Client fakeS3Client;
    private FakeSesClient fakeSesClient;

    static class FakeS3Client implements S3Client {
        @Override public void close() {}
        @Override
        public software.amazon.awssdk.services.s3.model.PutObjectResponse putObject(
                software.amazon.awssdk.services.s3.model.PutObjectRequest putObjectRequest,
                software.amazon.awssdk.core.sync.RequestBody requestBody) {
            return software.amazon.awssdk.services.s3.model.PutObjectResponse.builder().build();
        }
        @Override
        public String serviceName() {
            return "FakeS3";
        }
    }
    static class FakeSesClient implements SesClient {
        @Override public void close() {}
        @Override
        public software.amazon.awssdk.services.ses.model.SendEmailResponse sendEmail(
                software.amazon.awssdk.services.ses.model.SendEmailRequest sendEmailRequest) {
            return software.amazon.awssdk.services.ses.model.SendEmailResponse.builder().build();
        }
        @Override
        public String serviceName() {
            return "FakeSES";
        }
    }
    static class TestFeedbackRepository extends FeedbackRepository {
        public TestFeedbackRepository(software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient client) { super(client); }
        @Override public List<Feedback> findByDateRange(String start, String end) {
            Feedback feedback = new Feedback();
            feedback.setId("id");
            feedback.setDescription("desc");
            feedback.setRating(5);
            feedback.setUrgency("NORMAL");
            feedback.setCreatedAt(start);
            return Collections.singletonList(feedback);
        }
    }
    @BeforeEach
    void setUp() {
        software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient mockClient = org.mockito.Mockito.mock(software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient.class);
        testRepository = new TestFeedbackRepository(mockClient);
        fakeS3Client = new FakeS3Client();
        fakeSesClient = new FakeSesClient();
        service = new ReportService(testRepository, fakeS3Client, fakeSesClient, "bucket", "recipient@example.com");
    }
    @Test
    void testGenerateWeeklyReportDoesNotThrow() {
        assertDoesNotThrow(() -> service.generateWeeklyReport());
    }
}
