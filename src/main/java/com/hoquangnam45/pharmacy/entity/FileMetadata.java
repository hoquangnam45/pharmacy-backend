package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "file_metadata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String contentType;
    private String extension;
    private String path;
    private OffsetDateTime createdAt;

    @OneToOne(mappedBy = "fileMetadata")
    private UploadSessionFileMetadata uploadSessionFileMetadata;

    @OneToOne(mappedBy = "fileMetadata")
    private MedicinePreview medicinePreview;
}
