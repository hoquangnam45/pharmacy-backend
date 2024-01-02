package com.hoquangnam45.pharmacy.service.impl;

import com.hoquangnam45.pharmacy.service.IS3Service;
import org.apache.commons.io.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class MockS3Service implements IS3Service {
    @Override
    public void uploadFile(MultipartFile file, String key) throws IOException {
        File storedFile = new File(key);
        FileUtils.createParentDirectories(storedFile);
        storedFile.createNewFile();
        try (InputStream is = file.getInputStream();
             BufferedInputStream bis = new BufferedInputStream(is)) {
            FileUtils.copyInputStreamToFile(bis, storedFile);
        }
    }

    @Override
    public String getDownloadPath(String key) {
        return new File(key).getAbsoluteFile().toURI().normalize().getPath();
    }

    @Override
    public void deleteFile(String key) throws IOException {
        FileUtils.delete(new File(key));
    }

    @Override
    public void deleteFolder(String key) throws IOException {
        FileUtils.deleteDirectory(new File(key));
    }

    @Override
    public void copyFileToFile(String keyA, String keyB) throws IOException {
        FileUtils.copyFile(new File(keyA), new File(keyB));
    }

    @Override
    public void copyFolderToFolder(String folderA, String folderB) throws IOException {
        FileUtils.copyDirectory(new File(folderA), new File(folderB));
    }
}
