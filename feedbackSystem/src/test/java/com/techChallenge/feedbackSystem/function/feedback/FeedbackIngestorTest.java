package com.techChallenge.feedbackSystem.function.feedback;

import com.techChallenge.feedbackSystem.dto.feedback.FeedbackRequestDTO;
import com.techChallenge.feedbackSystem.service.feedback.FeedbackService;
import org.junit.jupiter.api.Test;
import java.util.function.Function;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackIngestorTest {
    static class FakeFeedbackService extends FeedbackService {
        public FakeFeedbackService() { super(null, null); }
        @Override public String processIngestion(FeedbackRequestDTO request) { return "OK"; }
    }
    @Test
    void testIngestFeedbackFunction() {
        FeedbackService fakeService = new FakeFeedbackService();
        FeedbackRequestDTO dto = new FeedbackRequestDTO();
        dto.setDescription("Teste");
        dto.setRating(5);
        FeedbackIngestor ingestor = new FeedbackIngestor(fakeService);
        Function<FeedbackRequestDTO, String> func = ingestor.ingestFeedback();
        String result = func.apply(dto);
        assertEquals("OK", result);
    }
}
