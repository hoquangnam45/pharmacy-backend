package com.hoquangnam45.pharmacy.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.hoquangnam45.pharmacy.config.UploadConfig;
import com.hoquangnam45.pharmacy.pojo.S3ConfigDetail;
import com.hoquangnam45.pharmacy.service.IS3Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;

public class S3Service implements IS3Service {
    private final AmazonS3 client;
    private final String defaultBucketName;

    public S3Service(UploadConfig uploadConfig) {
        S3ConfigDetail configDetail = uploadConfig.getS3();
        AWSCredentials credential = new BasicAWSCredentials(
                configDetail.getAccessKey(),
                configDetail.getSecretKey()
        );
        this.client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credential))
                .withRegion(configDetail.getRegion())
                .build();
        this.defaultBucketName = configDetail.getDefaultBucketName();
    }

    @Override
    public void uploadFile(MultipartFile file, String key) throws IOException {
        uploadFile(file, defaultBucketName, key, Collections.emptyMap());
    }

    @Override
    public String getDownloadPath(String key) {
        return client.getUrl(defaultBucketName, key).toString();
    }

    @Override
    public void deleteFile(String key) {
        client.deleteObject(defaultBucketName, key);
    }

    @Override
    public void deleteFolder(String key) {
        deleteFolder(defaultBucketName, key);
    }

    @Override
    public void copyFileToFile(String keyA, String keyB) {
        client.copyObject(defaultBucketName, keyA, defaultBucketName, keyB);
    }

    @Override
    public void copyFolderToFolder(String folderA, String folderB) {
        if (Objects.equals(folderA, folderB)) {
            return;
        }
        for (S3ObjectSummary file : listIn(defaultBucketName, folderA).getObjectSummaries()) {
            Path relativePath = Path.of(file.getKey()).relativize(Path.of(folderA));
            copyFileToFile(file.getKey(), Path.of(folderB, relativePath.toString()).toString());
        }
    }

    private void deleteFolder(String bucketName, String key) {
        for (S3ObjectSummary file : listIn(bucketName, key).getObjectSummaries()){
            client.deleteObject(bucketName, file.getKey());
        }
    }

    private void uploadFile(MultipartFile file, String bucketName, String key, Map<String, String> metadata) throws IOException {
        try (InputStream is = file.getInputStream()) {
            client.putObject(new PutObjectRequest(bucketName, key, is, metadata.entrySet()
                            .stream()
                            .collect(Collector.of(
                                    ObjectMetadata::new,
                                    (acc, entry) -> acc.addUserMetadata(entry.getKey(), entry.getValue()),
                                    (v1, v2) -> v1)))
            );
        }
    }

    private ListObjectsV2Result listIn(String bucketName, String folder) {
        return client.listObjectsV2(new ListObjectsV2Request()
                .withDelimiter("/")
                .withBucketName(bucketName)
                .withPrefix(folder));
    }
}
