package com.techChallenge.feedbackSystem.function.report;

import com.techChallenge.feedbackSystem.service.report.ReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class WeeklyReporter {

    private final ReportService reportService;

    public WeeklyReporter(ReportService reportService) {
        this.reportService = reportService;
    }

    @Bean
    public Consumer<Object> generateReport() {
        return event -> {
            System.out.println("Evento de agendamento recebido: " + event);
            reportService.generateWeeklyReport();
            System.out.println("Relatório semanal gerado com sucesso.");
        };
    }
}