package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "upload_session_file_metadata")
public class UploadSessionFileMetadata {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "file_metadata_id", referencedColumnName = "id", insertable = false, updatable = false)
    private FileMetadata fileMetadata;

    @ManyToOne
    @JoinColumn(name = "upload_session_id", referencedColumnName = "id")
    private UploadSession uploadSession;

    // Do this to avoid ambiguity with the other mapping
    @Column(name = "file_metadata_id")
    private UUID fileMetadataId;
}
