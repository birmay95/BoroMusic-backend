package com.example.music_platform.service;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;

@Service
@AllArgsConstructor
public class BackblazeFileService {


    private final Dotenv dotenv = Dotenv.load();  // Загрузка .env файла

    private final String accessKey = dotenv.get("AWS_ACCESS_KEY_ID");
    private final String secretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
    private final String endpoint = dotenv.get("ENDPOINT_URL");
    private final String bucketName = dotenv.get("BUCKET_NAME");

    private S3Client createS3Client() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        assert endpoint != null;
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .build();
    }

    public String uploadFile(String fileName, String uploadPath) {
        S3Client s3Client = createS3Client();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        try (InputStream inputStream = new FileInputStream(uploadPath)) {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, new File(uploadPath).length()));
            return "File uploaded successfully to bucket: " + bucketName + " with name: " + fileName;
        } catch (IOException e) {
            return "Error uploading file: " + e.getMessage();
        }
    }

    public String deleteFile(String fileName) {
        S3Client s3Client = createS3Client();

        try {
            ListObjectVersionsRequest listVersionsRequest = ListObjectVersionsRequest.builder()
                    .bucket(bucketName)
                    .prefix(fileName)
                    .build();

            ListObjectVersionsResponse versionsResponse = s3Client.listObjectVersions(listVersionsRequest);

            for (ObjectVersion version : versionsResponse.versions()) {
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(version.key())
                        .versionId(version.versionId())
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
            }

            return "File " + fileName + " fully deleted from bucket: " + bucketName;
        } catch (S3Exception e) {
            return "Error deleting file: " + e.getMessage();
        }
    }



    public void listBuckets() {
        S3Client s3Client = createS3Client();

        ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets(listBucketsRequest);

        listBucketsResponse.buckets().forEach(bucket -> System.out.println(bucket.name()));
    }

    public void downloadFile(String fileName, String downloadPath) {
        S3Client s3Client = createS3Client();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3Client.getObject(getObjectRequest, Paths.get(downloadPath));
        System.out.println("File downloaded successfully to: " + downloadPath);
    }

    public InputStream downloadFileStream(String fileName) {
        S3Client s3Client = createS3Client();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        return s3Client.getObject(getObjectRequest);
    }
}
