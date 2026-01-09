package com.techChallenge.feedbackSystem.domain.report;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WeeklyReportTest {
    @Test
    void testGettersAndSetters() {
        WeeklyReport report = new WeeklyReport();
        report.setReportKey("RPT-001");
        report.setPeriod("2026-W01");
        report.setAverageRating(4.5);
        report.setTotalFeedbacks(10L);
        report.setCreatedAt("2026-01-08");

        assertEquals("RPT-001", report.getReportKey());
        assertEquals("2026-W01", report.getPeriod());
        assertEquals(4.5, report.getAverageRating());
        assertEquals(10L, report.getTotalFeedbacks());
        assertEquals("2026-01-08", report.getCreatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        WeeklyReport report = new WeeklyReport("RPT-002", "2026-W02", 3.2, 5L, "2026-01-15");
        assertEquals("RPT-002", report.getReportKey());
        assertEquals("2026-W02", report.getPeriod());
        assertEquals(3.2, report.getAverageRating());
        assertEquals(5L, report.getTotalFeedbacks());
        assertEquals("2026-01-15", report.getCreatedAt());
    }
}
