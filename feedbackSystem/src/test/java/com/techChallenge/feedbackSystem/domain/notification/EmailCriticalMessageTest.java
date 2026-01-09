package com.techChallenge.feedbackSystem.domain.notification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailCriticalMessageTest {
    @Test
    void testGettersAndSetters() {
        EmailCriticalMessage msg = new EmailCriticalMessage();
        msg.setDescription("Falha crítica");
        msg.setUrgency("alta");
        msg.setSentAt("2026-01-08");

        assertEquals("Falha crítica", msg.getDescription());
        assertEquals("alta", msg.getUrgency());
        assertEquals("2026-01-08", msg.getSentAt());
    }

    @Test
    void testAllArgsConstructor() {
        EmailCriticalMessage msg = new EmailCriticalMessage("Erro", "baixa", "2026-01-07");
        assertEquals("Erro", msg.getDescription());
        assertEquals("baixa", msg.getUrgency());
        assertEquals("2026-01-07", msg.getSentAt());
    }
}
