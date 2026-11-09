package br.com.process.config;

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Factory
public class AwsS3Configuration {

    @Bean
    S3Client s3Client(S3Properties properties) {
        var client = S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
        return client;
//        return S3Client.builder()
//                .region(Region.of(properties.getRegion()))
//                .credentialsProvider(DefaultCredentialsProvider.create())
//                .build();
    }
}
