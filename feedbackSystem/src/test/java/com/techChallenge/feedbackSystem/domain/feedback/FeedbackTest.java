package com.techChallenge.feedbackSystem.domain.feedback;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest {
    @Test
    void testGettersAndSetters() {
        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setDescription("Ótimo serviço");
        feedback.setRating(5);
        feedback.setUrgency("alta");
        feedback.setCreatedAt("2026-01-08");

        assertEquals("1", feedback.getId());
        assertEquals("Ótimo serviço", feedback.getDescription());
        assertEquals(5, feedback.getRating());
        assertEquals("alta", feedback.getUrgency());
        assertEquals("2026-01-08", feedback.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        Feedback feedback = new Feedback("2", "Ruim", 1, "baixa", "2026-01-07");
        assertEquals("2", feedback.getId());
        assertEquals("Ruim", feedback.getDescription());
        assertEquals(1, feedback.getRating());
        assertEquals("baixa", feedback.getUrgency());
        assertEquals("2026-01-07", feedback.getCreatedAt());
    }
}
