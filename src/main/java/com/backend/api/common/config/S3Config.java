package com.backend.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

@Configuration
public class S3Config {

    private final Environment env;

    public S3Config(Environment env) {
        this.env = env;
    }

    @Bean
    public S3Client s3Client() {
        String region = env.getProperty("aws.region.static", "ap-northeast-2");

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
//                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        String region = env.getProperty("aws.region.static", "ap-northeast-2");

        return S3AsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
//                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    @Bean
    public S3TransferManager transferManager() {
        String region = env.getProperty("aws.region.static", "ap-northeast-2");

        return S3TransferManager.builder()
                .s3Client(S3AsyncClient.builder()
                        .region(Region.of(region))
                        .credentialsProvider(DefaultCredentialsProvider.create())
//                        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                        .build())
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {

        String region = env.getProperty("aws.region.static", "ap-northeast-2");

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}