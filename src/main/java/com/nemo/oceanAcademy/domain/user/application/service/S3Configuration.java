package com.nemo.oceanAcademy.domain.user.application.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Configuration {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of("ap-northeast-2")) // 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(
                        "your-access-key-id",
                        "your-secret-access-key"
                )))
                .build();
    }
}
