package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.config.UploadConfig;
import com.hoquangnam45.pharmacy.entity.FileMetadata;
import com.hoquangnam45.pharmacy.entity.UploadSession;
import com.hoquangnam45.pharmacy.entity.UploadSessionFileMetadata;
import com.hoquangnam45.pharmacy.exception.UploadSessionInvalidException;
import com.hoquangnam45.pharmacy.pojo.UploadSessionConfig;
import com.hoquangnam45.pharmacy.repo.FileMetadataRepo;
import com.hoquangnam45.pharmacy.repo.UploadSessionFileMetadataRepo;
import com.hoquangnam45.pharmacy.repo.UploadSessionRepo;
import com.hoquangnam45.pharmacy.service.impl.S3Service;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class UploadSessionService {
    private final Map<String, UploadSessionConfig> uploadSessionConfigs;
    private final UploadSessionRepo uploadSessionRepo;
    private final FileMetadataRepo fileMetadataRepo;
    private final IS3Service s3Service;
    private final UploadSessionFileMetadataRepo uploadSessionFileMetadataRepo;

    public UploadSessionService(UploadConfig uploadConfig, UploadSessionRepo uploadSessionRepo, FileMetadataRepo fileMetadataRepo, IS3Service s3Service, UploadSessionFileMetadataRepo uploadSessionFileMetadataRepo) {
        this.uploadSessionRepo = uploadSessionRepo;
        this.fileMetadataRepo = fileMetadataRepo;
        this.s3Service = s3Service;
        this.uploadSessionFileMetadataRepo = uploadSessionFileMetadataRepo;
        this.uploadSessionConfigs = uploadConfig.getSessions().stream().collect(Collectors.toMap(
                UploadSessionConfig::getType,
                sessionConfig -> sessionConfig
        ));
    }

    private UploadSessionConfig getUploadSessionConfig(String type) {
        return Optional.ofNullable(uploadSessionConfigs.get(type))
                .orElseThrow(() -> new IllegalStateException("Upload session config for type " + type + " not registered yet"));
    }

    public UploadSession createUploadSession(String type) {
        UploadSessionConfig config = getUploadSessionConfig(type);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiredAt = now.plus(config.getExpiredDurationInMin());
        UploadSession session = new UploadSession();
        session.setType(type);
        session.setCreatedAt(now);
        session.setExpiredAt(expiredAt);
        session.setUploadSessionFileMetadatas(IntStream.range(0, config.getMaximumFileCount())
                .mapToObj(idx -> new UploadSessionFileMetadata())
                .peek(metadata -> metadata.setUploadSession(session))
                .collect(Collectors.toSet()));
        return uploadSessionRepo.save(session);
    }

    public void renewSession(String type, UUID sessionId) {
        if (hasSessionExpired(sessionId)) {
            return;
        }
        UploadSessionConfig config = getUploadSessionConfig(type);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        UploadSession currentSession = uploadSessionRepo.findById(sessionId).orElseThrow(() -> new IllegalStateException("Not possible to enter here"));
        currentSession.setExpiredAt(now.plus(config.getExpiredDurationInMin()));
    }

    public boolean hasSessionExpired(UUID sessionId) {
        return uploadSessionRepo.findById(sessionId)
                .map(uploadSession -> uploadSession.getExpiredAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC)))
                .orElse(true);
    }

    public boolean allowItemId(UUID sessionId, UUID fileId) {
        return uploadSessionRepo.findById(sessionId)
                .map(UploadSession::getUploadSessionFileMetadatas)
                .stream()
                .flatMap(Set::stream)
                .map(UploadSessionFileMetadata::getId)
                .anyMatch(uploadFileMetadataId -> uploadFileMetadataId.equals(fileId));
    }

    public String getTempSessionFileUploadKey(String type, UUID sessionId, String fileName) {
        return MessageFormat.format("{0}/{1}", getTempSessionUploadFolder(type, sessionId), fileName);
    }

    public String getTempSessionUploadFolder(String type, UUID sessionId) {
        return MessageFormat.format("{0}/tmp/{1}", uploadSessionConfigs.get(type).getPrefix(), sessionId);
    }

    public String getFinalUploadFolder(String type, UUID sessionId) {
        return MessageFormat.format("{0}/{1}", uploadSessionConfigs.get(type).getPrefix(), sessionId);
    }

    public void storeTempFileMetadata(UUID sessionId, UUID uploadSessionFileMetadataId, String fileName, String extension, String contentType, String filePath) throws IOException {
        // Delete previous file
        UploadSessionFileMetadata uploadSessionFileMetadata = uploadSessionFileMetadataRepo.findByUploadSession_IdAndId(sessionId, uploadSessionFileMetadataId);
        if (uploadSessionFileMetadata == null) {
            throw new UploadSessionInvalidException("File id not allowed for this session");
        }
        FileMetadata fileMetadata = uploadSessionFileMetadata.getFileMetadata();
        if (fileMetadata != null) {
            s3Service.deleteFile(fileMetadata.getPath());
            fileMetadataRepo.delete(fileMetadata);
        }
        FileMetadata metadata = new FileMetadata();
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        metadata.setCreatedAt(now);
        metadata.setPath(filePath);
        metadata.setName(fileName);
        metadata.setContentType(contentType);
        metadata.setUploadSessionFileMetadata(uploadSessionFileMetadata);
        metadata.setExtension(extension);
        fileMetadataRepo.save(metadata);
    }

    public UploadSession getUploadSession(UUID id) {
        return uploadSessionRepo.findById(id).orElse(null);
    }

    public List<UploadSession> getAllExpiredUploadSession() {
        return uploadSessionRepo.findAllByExpiredAtLessThan(OffsetDateTime.now(ZoneOffset.UTC));
    }

    public void deleteSession(UploadSession uploadSession) throws IOException {
        s3Service.deleteFolder(getTempSessionUploadFolder(uploadSession.getType(), uploadSession.getId()));
        Set<UploadSessionFileMetadata> uploadSessionFileMetadatas = uploadSession.getUploadSessionFileMetadatas();
        uploadSessionFileMetadatas.forEach(uploadSessionFileMetadataRepo::delete);
        uploadSessionFileMetadatas.stream().map(UploadSessionFileMetadata::getFileMetadataId).forEach(fileMetadataRepo::deleteById);
        uploadSessionRepo.delete(uploadSession);
    }
}
