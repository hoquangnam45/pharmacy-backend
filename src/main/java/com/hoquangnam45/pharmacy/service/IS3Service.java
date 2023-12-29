package com.hoquangnam45.pharmacy.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IS3Service {
    void uploadFile(MultipartFile file, String key) throws IOException;
    String getDownloadPath(String key) throws IOException;
    void deleteFile(String key) throws IOException;
    void deleteFolder(String key) throws IOException;
    void copyFileToFile(String keyA, String keyB) throws IOException;
    void copyFolderToFolder(String folderA, String folderB) throws IOException;
}
