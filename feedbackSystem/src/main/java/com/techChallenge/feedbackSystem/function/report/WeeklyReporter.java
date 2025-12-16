package com.techChallenge.feedbackSystem.function.report;

import com.techChallenge.feedbackSystem.service.report.ReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class WeeklyReporter {

    private final ReportService reportService;

    public WeeklyReporter(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * O Bean Runnable é usado para tarefas agendadas (EventBridge/Scheduler).
     * Ele é acionado no agendamento e simplesmente executa a lógica do Service.
     */
    @Bean
    public Runnable generateReport() {
        return reportService::generateWeeklyReport;
    }
}