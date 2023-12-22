package com.hoquangnam45.pharmacy.repo;

import com.hoquangnam45.pharmacy.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileMetadataRepo extends JpaRepository<FileMetadata, UUID> {
    List<FileMetadata> findAllByUploadSessionFileMetadata_UploadSession_Id(UUID id);
    List<FileMetadata> findAllByMedicinePreview_Medicine_Id(UUID id);
}
