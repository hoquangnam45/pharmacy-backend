package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "upload_session")
public class UploadSession {
    @Id
    @GeneratedValue
    private UUID id;
    private String type;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiredAt;

    @OneToMany(mappedBy = "uploadSession")
    private Set<UploadSessionFileMetadata> uploadSessionFileMetadatas;
}
