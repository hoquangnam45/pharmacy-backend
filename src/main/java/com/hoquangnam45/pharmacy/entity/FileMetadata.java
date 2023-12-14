package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue
    private UUID id;
    private String fileName;
    private String contentType;
    private String fileHash;
    private String path;
    private OffsetDateTime createdAt;
}
