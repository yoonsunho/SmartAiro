package com.example.smartAir.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

// S3 파일 업로드 및 Presigned URL 관리 서비스

@Service
public class AwsS3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    // S3 버킷 이름은 설정 파일(application.properties)에서 읽어옴
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region}")
    private String regionName;

    public AwsS3Service(S3Client s3Client,
                        @Value("${cloud.aws.credentials.access-key}") String accessKey,
                        @Value("${cloud.aws.credentials.secret-key}") String secretKey,
                        @Value("${cloud.aws.region}")String region) {
        this.s3Client = s3Client;

        // Presigner는 별도로 생성 (반드시 region, credentials 지정)
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        this.s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }
    

    // 파일을 S3에 업로드
    public String uploadFile(String orinId, MultipartFile file)throws IOException {

        // S3 내에 "maps/{orinId}/{timestamp}_원본파일명" 경로로 저장
        String key = "maps/"+ orinId +"/"+System.currentTimeMillis() + "_"
                + file.getOriginalFilename();
        
        // S3 업로드용 요청 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        // 파일 실제 업로드
        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );

        // 업로드 후 key 반환
        return key;
    }

    /*
     * Presigned URL 생성 및 반환
     * @param key S3 버킷 내 파일 경로 (uploadFile에서 반환된 key)
     * @param duration초단위 presigned URL 유효 기간 (예: 5분=300초)
     * @return 임시 접근 가능한 URL 문자열
     */
    public String generatePresignedUrl(String key, Duration duration) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(duration)
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

        return presignedRequest.url().toString();
    }

    public String getBucketName() { return this.bucketName; }




}
