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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7);

        List<Feedback> feedbacks = repository.findByDateRange(start, end);

        double avgRating = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        String reportContent = formatReport(feedbacks.size(), avgRating, start, end);

        String key = String.format("reports/%s_weekly_report.txt", start.toLocalDate());
        saveReportToS3(key, reportContent);

        sendEmailNotification(avgRating, feedbacks.size(), key);
    }

    private String formatReport(int total, double avgRating, LocalDateTime start, LocalDateTime end) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
                "Relatório Semanal de Feedback\n" +
                        "Período: %s a %s\n" +
                        "----------------------------------------\n" +
                        "Total de Feedbacks Recebidos: %d\n" +
                        "Média de Avaliações: %.2f\n" +
                        "Status: %s",
                start.format(formatter), end.format(formatter),
                total, avgRating,
                (avgRating < 7.0) ? " ATENÇÃO: Média abaixo da meta (7.0)" : " Média dentro da meta"
        );
    }

    private void saveReportToS3(String key, String content) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("text/plain")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
        System.out.println("Relatório salvo no S3: s3://" + bucketName + "/" + key);
    }

    private void sendEmailNotification(double avgRating, int total, String s3Key) {
        String subject = String.format("Relatório Semanal de Feedback - Média: %.2f", avgRating);
        String bodyText = String.format(
                "O relatório semanal foi gerado.\nTotal de Feedbacks: %d\nMédia: %.2f\n" +
                        "O relatório completo está disponível em: s3://%s/%s",
                total, avgRating, bucketName, s3Key
        );

        Destination destination = Destination.builder().toAddresses(recipientEmail).build();
        Content content = Content.builder().data(bodyText).build();
        Body body = Body.builder().text(content).build();
        Message message = Message.builder().subject(Content.builder().data(subject).build()).body(body).build();

        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .source(SENDER_EMAIL)
                .destination(destination)
                .message(message)
                .build();

        sesClient.sendEmail(sendEmailRequest);
    }
}