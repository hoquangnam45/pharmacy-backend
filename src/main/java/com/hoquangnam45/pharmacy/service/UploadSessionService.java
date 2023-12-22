package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.entity.FileMetadata;
import com.hoquangnam45.pharmacy.entity.UploadSession;
import com.hoquangnam45.pharmacy.entity.UploadSessionFileMetadata;
import com.hoquangnam45.pharmacy.exception.UploadSessionInvalidException;
import com.hoquangnam45.pharmacy.pojo.UploadSessionConfig;
import com.hoquangnam45.pharmacy.repo.FileMetadataRepo;
import com.hoquangnam45.pharmacy.repo.UploadSessionFileMetadataRepo;
import com.hoquangnam45.pharmacy.repo.UploadSessionRepo;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class UploadSessionService {
    private final Map<String, UploadSessionConfig> uploadSessionConfigs = new HashMap<>();
    private final UploadSessionRepo uploadSessionRepo;
    private final FileMetadataRepo fileMetadataRepo;
    private final S3Service s3Service;
    private final UploadSessionFileMetadataRepo uploadSessionFileMetadataRepo;

    public void registerUploadSessionConfig(String type, UploadSessionConfig sessionConfig) {
        uploadSessionConfigs.putIfAbsent(type, sessionConfig);
    }

    private UploadSessionConfig getUploadSessionConfig(String type) {
        return Optional.ofNullable(uploadSessionConfigs.get(type))
                .orElseThrow(() -> new IllegalStateException("Upload session config for type " + type + " not registered yet"));
    }

    public UploadSession createUploadSession(String type) {
        UploadSessionConfig config = getUploadSessionConfig(type);
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiredAt = now.plus(config.getExpiredDuration());
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
        currentSession.setExpiredAt(now.plus(config.getExpiredDuration()));
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

    public String getTempSessionFileUploadKey(String type, String sessionId, String fileName) {
        return MessageFormat.format("{0}/{1}", getTempSessionUploadFolder(type, sessionId), fileName);
    }

    public String getTempSessionUploadFolder(String type, String sessionId) {
        return MessageFormat.format("{0}/tmp/{1}", type, sessionId);
    }

    public String getFinalUploadFolder(String type, String sessionId) {
        return MessageFormat.format("{0}/{1}", type, sessionId);
    }

    public FileMetadata storeTempFileMetadata(UUID sessionId, UUID uploadSessionFileMetadataId, String fileName, String extension, String contentType, String filePath) {
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
        return fileMetadataRepo.save(metadata);
    }

    public UploadSession getUploadSession(UUID id) {
        return uploadSessionRepo.findById(id).orElse(null);
    }

    public List<UploadSession> getAllExpiredUploadSession() {
        return uploadSessionRepo.findAllByExpiredAtLessThan(OffsetDateTime.now(ZoneOffset.UTC));
    }

    public void deleteSession(UploadSession uploadSession) {
        s3Service.deleteFolder(getTempSessionUploadFolder(uploadSession.getType(), uploadSession.getId().toString()));
        uploadSession.getUploadSessionFileMetadatas()
                .forEach(uploadSessionFileMetadataRepo::delete);
        uploadSessionRepo.delete(uploadSession);
    }
}
