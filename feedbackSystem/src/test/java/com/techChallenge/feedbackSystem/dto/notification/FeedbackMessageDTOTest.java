package com.techChallenge.feedbackSystem.dto.notification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackMessageDTOTest {
    @Test
    void testGettersAndSetters() {
        FeedbackMessageDTO dto = new FeedbackMessageDTO();
        dto.setFeedbackId("fb-123");
        assertEquals("fb-123", dto.getFeedbackId());
    }
}
