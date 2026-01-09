package com.techChallenge.feedbackSystem.dto.feedback;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackRequestDTOTest {
    @Test
    void testGettersAndSetters() {
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setDescription("Bom atendimento");
        dto.setRating(4);

        assertEquals("Bom atendimento", dto.getDescription());
        assertEquals(4, dto.getRating());
    }
}
