package br.com.item.config.sqs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

@Configuration
public class SqsConfig {

    @Value("${aws.region}")
    private String region;

    @Bean
    @Profile("local")
    public SqsClient localSqsClient() {
        return SqsClient.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .build();
    }

    @Bean
    @Profile("prod")
    public SqsClient prodSqsClient() {
        return SqsClient.builder()
                .region(Region.of(region))
                .build(); // Usa IAM Role automaticamente
    }
}
