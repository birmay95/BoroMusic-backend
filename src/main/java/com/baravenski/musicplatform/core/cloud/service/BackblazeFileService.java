package com.baravenski.musicplatform.core.cloud.service;

import com.baravenski.musicplatform.exception.impl.UploadTrackToTheMlOrAwsServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackblazeFileService {

    @Value("${backblaze.bucket.name}")
    private String bucketName;

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;

    public void uploadFile(String fileName, String uploadPath) {
        var putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (InputStream inputStream = new FileInputStream(uploadPath)) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, new File(uploadPath).length()));
        } catch (IOException ioException) {
            throw new UploadTrackToTheMlOrAwsServiceException();
        }
    }

    public void deleteFile(String fileName) {
        var listVersionsRequest = ListObjectVersionsRequest.builder()
                .bucket(bucketName)
                .prefix(fileName)
                .build();
        var versionsResponse = s3Client.listObjectVersions(listVersionsRequest);

        for (var version : versionsResponse.versions()) {
            var deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(version.key())
                    .versionId(version.versionId())
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        }
    }

    public void listBuckets() {
        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);

        listBucketsResponse.buckets().forEach(bucket -> System.out.println(bucket.name()));
    }

    public void downloadFile(String fileName, String downloadPath) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
    }

    public InputStream downloadFileStream(String fileName) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            return s3Client.getObject(getObjectRequest);
        } catch (Exception e) {
            log.error("Error when downloading: {}", e.getMessage());
            return null;
        }
    }


    public String generateTemporaryUrl(String objectKey) {
        log.info("[CLOUD-STORAGE] Requesting secure Presigned URL for file: {}", objectKey);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(60))
                .build();

        log.info("[CLOUD-STORAGE] Presigned URL generated successfully (Valid for 60 minutes)");
        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}
