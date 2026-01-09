package com.techChallenge.feedbackSystem.function.report;

import com.techChallenge.feedbackSystem.service.report.ReportService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.function.Consumer;

class WeeklyReporterTest {
    static class FakeReportService extends ReportService {
        public FakeReportService() { super(null, null, null, "", ""); }
        @Override public void generateWeeklyReport() {
            
        }
    }
    @Test
    void testGenerateReport() {
        ReportService fakeService = new FakeReportService();
        WeeklyReporter reporter = new WeeklyReporter(fakeService);
        Consumer<Object> consumer = reporter.generateReport();
        try {
            consumer.accept("event");
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }
}
