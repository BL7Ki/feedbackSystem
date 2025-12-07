package com.techChallenge.feedbackSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AWSConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.create();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.create();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.create();
    }

    @Bean
    public software.amazon.awssdk.services.s3.S3Client s3Client() {
        return software.amazon.awssdk.services.s3.S3Client.create();
    }

    @Bean
    public software.amazon.awssdk.services.ses.SesClient sesClient() {
        return software.amazon.awssdk.services.ses.SesClient.create();
    }
}
