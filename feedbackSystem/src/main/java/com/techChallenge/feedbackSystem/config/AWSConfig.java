package com.techChallenge.feedbackSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sns.SnsClient;

@Configuration
public class AWSConfig {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(software.amazon.awssdk.services.dynamodb.DynamoDbClient.builder()
                        .region(Region.of(awsRegion))
                        .build())
                .build();
    }

    @Bean
    public SqsClient sqsClient() {
        return SqsClient.builder().region(Region.of(awsRegion)).build();
    }

    @Bean
    public SnsClient snsClient() {
        return SnsClient.builder().region(Region.of(awsRegion)).build();
    }

    @Bean
    public software.amazon.awssdk.services.s3.S3Client s3Client() {
        return software.amazon.awssdk.services.s3.S3Client.builder().region(Region.of(awsRegion)).build();
    }

    @Bean
    public software.amazon.awssdk.services.ses.SesClient sesClient() {
        return software.amazon.awssdk.services.ses.SesClient.builder().region(Region.of(awsRegion)).build();
    }
}
