package com.example.smartAir.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// AWS SDK S3용 임포트
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/*
AWS S3 SDK 클라이언트 빈을 생성하는 Spring 설정 클래스
액세스키, 시크릿 키, 리전은 application.properties에서 주입받음
 */

@Configuration
public class AwsS3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;
    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region}")
    private String region;

    // S3Client 를 Bean으로 등록
    // DI방식으로 다른 서비스에 주입됨
    @Bean
    public S3Client s3Client(){
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))  // AWS 리전 (예: ap-northeast-2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();

    }

}
