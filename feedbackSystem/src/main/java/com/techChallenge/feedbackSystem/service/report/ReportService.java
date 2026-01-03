package com.techChallenge.feedbackSystem.service.report;

import com.techChallenge.feedbackSystem.domain.feedback.Feedback;
import com.techChallenge.feedbackSystem.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final FeedbackRepository repository;
    private final S3Client s3Client;
    private final SesClient sesClient;
    private final String bucketName;
    private final String recipientEmail;
    private static final String SENDER_EMAIL = "leoventura245@gmail.com";

    public ReportService(
            FeedbackRepository repository,
            S3Client s3Client,
            SesClient sesClient,
            @Value("${aws.s3.reportsBucketName}") String bucketName,
            @Value("${aws.ses.reportRecipientEmail}") String recipientEmail) {
        this.repository = repository;
        this.s3Client = s3Client;
        this.sesClient = sesClient;
        this.bucketName = bucketName;
        this.recipientEmail = recipientEmail;
    }

    public void generateWeeklyReport() {
        String end = Instant.now().toString();
        String start = Instant.now().minus(7, ChronoUnit.DAYS).toString();

        List<Feedback> feedbacks = repository.findByDateRange(start, end);

        double avgRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        Map<String, Long> countByUrgency = feedbacks.stream()
                .collect(Collectors.groupingBy(Feedback::getUrgency, Collectors.counting()));

        String reportContent = formatReport(feedbacks.size(), avgRating, countByUrgency, start, end);

        String key = String.format("reports/%s_weekly_report.txt", start.substring(0, 10));
        saveReportToS3(key, reportContent);

        sendEmailNotification(avgRating, feedbacks.size(), key);
    }

    private String formatReport(int total, double avgRating, Map<String, Long> urgencyMap, String start, String end) {
        StringBuilder sb = new StringBuilder();
        sb.append("Relatório Semanal de Feedback\n");
        sb.append(String.format("Período: %s a %s\n", start, end));
        sb.append("----------------------------------------\n");
        sb.append(String.format("Total de Feedbacks Recebidos: %d\n", total));
        sb.append(String.format("Média de Avaliações: %.2f\n", avgRating));

        sb.append("\nDistribuição por Urgência:\n");
        urgencyMap.forEach((urgency, count) ->
                sb.append(String.format("- %s: %d\n", urgency, count))
        );

        sb.append("\nStatus: ").append((avgRating < 7.0) ? "ATENÇÃO: Média abaixo da meta" : "Média dentro da meta");

        return sb.toString();
    }

    private void saveReportToS3(String key, String content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
    }

    private void sendEmailNotification(double avgRating, int total, String s3Key) {
        String subject = String.format("Relatório Semanal de Feedback - Média: %.2f", avgRating);
        String bodyText = String.format(
                "O relatório semanal foi gerado com sucesso.\n\n" +
                        "Total de Feedbacks: %d\n" +
                        "Média: %.2f\n" +
                        "O relatório completo está disponível no S3: %s",
                total, avgRating, s3Key
        );

        sesClient.sendEmail(SendEmailRequest.builder()
                .source(SENDER_EMAIL)
                .destination(Destination.builder().toAddresses(recipientEmail).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder().text(Content.builder().data(bodyText).build()).build())
                        .build())
                .build());
    }
}