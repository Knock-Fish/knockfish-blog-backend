package com.knockfish.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Component
@ConfigurationProperties(prefix = "cloud.aws.s3")
@Data
public class R2FileConfig {
    private String endpoint;
    private String region;
    private String bucketName;
    private String cdnDomain;
    private Credentials credentials;
    @Data
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        this.credentials.getAccessKey(),
                        this.credentials.getSecretKey()
                ))
                .region(Region.of(region))
                .build();
    }
}
