package com.techChallenge.feedbackSystem.function.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techChallenge.feedbackSystem.dto.notification.FeedbackMessageDTO;
import com.techChallenge.feedbackSystem.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import java.util.Collections;
import java.util.function.Consumer;

class NotificationProcessorTest {
    static class FakeNotificationService extends NotificationService {
        public FakeNotificationService() { super(null, null); }
        @Override public void processMessage(FeedbackMessageDTO message) {
            
        }
    }
    @Test
    void testProcessNotifications() throws Exception {
        NotificationService fakeService = new FakeNotificationService();
        ObjectMapper objectMapper = new ObjectMapper();
        NotificationProcessor processor = new NotificationProcessor(fakeService, objectMapper);
        Consumer<SQSEvent> consumer = processor.processNotifications();

        FeedbackMessageDTO dto = new FeedbackMessageDTO();
        dto.setFeedbackId("fb-1");
        String body = objectMapper.writeValueAsString(dto);

        SQSMessage sqsMessage = new SQSMessage();
        sqsMessage.setBody(body);
        sqsMessage.setMessageId("msg-1");
        SQSEvent event = new SQSEvent();
        event.setRecords(Collections.singletonList(sqsMessage));

        try {
            consumer.accept(event);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
