package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.UploadSessionFileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UploadSessionFileMetadataRepo extends JpaRepository<UploadSessionFileMetadata, UUID> {
    UploadSessionFileMetadata findByUploadSession_IdAndId(UUID sessionId, UUID id);
    List<UploadSessionFileMetadata> findAllByUploadSession_Id(UUID sessionId);
}
