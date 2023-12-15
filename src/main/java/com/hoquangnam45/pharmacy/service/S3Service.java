package com.hoquangnam45.pharmacy.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.hoquangnam45.pharmacy.pojo.S3ConfigDetail;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

@Service
public class S3Service {
    private final AmazonS3 client;
    private final String defaultBucketName;

    public S3Service(S3ConfigDetail configDetail) {
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

    public void uploadFile(MultipartFile file, String key, Map<String, String> metadata) throws IOException {
        uploadFile(file, defaultBucketName, key, metadata);
    }

    public void uploadFile(MultipartFile file, String bucketName, String key, Map<String, String> metadata) throws IOException {
        try (InputStream is = file.getInputStream()) {
            client.putObject(new PutObjectRequest(bucketName, key, is, metadata.entrySet()
                            .stream()
                            .collect(Collector.of(
                                    ObjectMetadata::new,
                                    (acc, entry) -> acc.addUserMetadata(entry.getKey(), entry.getValue()),
                                    (v1, v2) -> v1))
                    )
            );
        }
    }

    public String getUrl(String bucketName, String key) {
        return client.getUrl(bucketName, key).toString();
    }

    public String getUrl(String key) {
        return client.getUrl(defaultBucketName, key).toString();
    }

    public void deleteFile(String bucketName, String key) {
        client.deleteObject(bucketName, key);
    }

    public void deleteFolder(String bucketName, String key) {
        for (S3ObjectSummary file : client.listObjectsV2(bucketName, key).getObjectSummaries()){
            client.deleteObject(bucketName, file.getKey());
        }
    }

    public void deleteFolder(String key) {
        deleteFolder(defaultBucketName, key);
    }

    public void copyFileToFile(String bucketA, String keyA, String bucketB, String keyB) {
        client.copyObject(bucketA, keyA, bucketB, keyB);
    }

    public void copyContentOfFolderToFolder(String bucketA, String folderA, String bucketB, String folderB) {
        for (S3ObjectSummary file : client.listObjectsV2(bucketA, folderA).getObjectSummaries()) {
            Path relativePath = Path.of(file.getKey()).relativize(Path.of(folderA));
            copyFileToFile(bucketA, file.getKey(), bucketB, Path.of(folderB, relativePath.toString()).toString());
        }
    }

    public void copyContentOfFolderToFolder(String folderA, String folderB) {
        copyContentOfFolderToFolder(defaultBucketName, folderA, defaultBucketName, folderB);
    }

    public void copyFolderToFolder(String folderA, String folderB) {
        copyContentOfFolderToFolder(defaultBucketName, folderA, defaultBucketName, folderB + "/" + Path.of(folderA).getFileName());
    }

    public List<String> listFolderIn(String folder) {
        return listFolderIn(defaultBucketName, folder);
    }

    public List<String> listFolderIn(String bucketName, String folder) {
        return client.listObjectsV2(new ListObjectsV2Request()
                .withDelimiter("/")
                .withBucketName(bucketName)
                .withPrefix(folder)).getCommonPrefixes();
    }
}
