package com.goott.potatoes.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Service
public class AwsS3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${spring.cloud.aws.credentials.bucket}")
    private String bucketName;

    @Value("${spring.cloud.aws.credentials.accessKey}")
    private String accessKeyId;

    @Value("${spring.cloud.aws.credentials.secretKey}")
    private String secretAccessKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    // url 가져오기
    public String getUrl(String key){
        // Presigner를 사용해 서명된 URL 생성
        S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .region(Region.of(region))
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60))  // URL 유효 시간 60분
                .getObjectRequest(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build())
                .build();

        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        presigner.close();

        return presignedRequest.url().toString();
    }

    // 이미지 파일 업로드
    public String uploadMultipartFile(MultipartFile file, String key) throws IOException {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        // 파일을 S3에 업로드
        PutObjectResponse response = s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        if (response.sdkHttpResponse().isSuccessful()) {
            // 업로드가 성공했을 경우, 파일의 URL 반환
            return getUrl(key);
        } else {
            // 업로드가 실패했을 경우, 예외 발생
            System.out.println("aws s3 업로드 실패");
            return null;
        }
    }

    // 파일 삭제
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }
}
