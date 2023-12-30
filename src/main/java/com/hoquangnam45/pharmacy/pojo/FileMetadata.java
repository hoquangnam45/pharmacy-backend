package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.entity.MedicinePreview;
import com.hoquangnam45.pharmacy.entity.MedicinePreviewAudit;
import com.hoquangnam45.pharmacy.entity.UploadSessionFileMetadata;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
public class FileMetadata {
    private final UUID id;
    private final String name;
    private final String contentType;
    private final String extension;
    private final String path;
    private final String downloadPath;
    private final OffsetDateTime createdAt;
    private final UUID uploadSessionFileId;
    private final UUID uploadSessionId;
}

