package com.hoquangnam45.pharmacy.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.hoquangnam45.pharmacy.config.UploadConfig;
import com.hoquangnam45.pharmacy.pojo.S3UploadConfigDetail;
import com.hoquangnam45.pharmacy.pojo.UploadSessionConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class S3UploadService {
    // could be uploaded image for product, avatar
    // object type -> upload session config
    private final Map<String, UploadSessionConfig> sessionConfigs = new HashMap<>();
    private final S3UploadConfigDetail uploadConfig;
    private final String rootBucketName;
    private final AmazonS3 client;

    public S3UploadService(S3UploadConfigDetail uploadConfig) {
        this.uploadConfig = uploadConfig;
        AWSCredentials credential = new BasicAWSCredentials(
                uploadConfig.getAccessKey(),
                uploadConfig.getSecretKey()
        );
        this.rootBucketName = uploadConfig.getRootBucketName();
        this.client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credential))
                .withRegion(uploadConfig.getRegion())
                .build();
    }

    public void registerSessionType(String objectType, UploadSessionConfig uploadSessionConfig) {
        sessionConfigs.putIfAbsent(objectType, uploadSessionConfig);
    }

    public String createTempUploadSession(UploadSessionConfig sessionConfig) {
        return UUID.randomUUID().toString();
    }

    public void uploadFile(File file, String objectType, String sessionId, String fileId, String fileName) {
        UploadSessionConfig sessionConfig = getSessionConfig(objectType);
    }

    private UploadSessionConfig getSessionConfig(String objectType) {
        return sessionConfigs.get(objectType);
    }

    private String getTempFileUploadKey(String objectType, String sessionId, String fileId, String fileName) {
        return MessageFormat.format("/{0}/{1}/tmp/{2}/{3}", rootBucketName, objectType, sessionId, fileId, fileName))
    }
}
