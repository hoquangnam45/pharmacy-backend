package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

// Why have a separate table for preview, one use-case is after you uploaded the file,
// you can keep the order of the original uploaded by sorting the created at field
// but in case where you want to rearrange the preview after you uploaded, then you do
// not need to modify the metadata created at field, just delete a row in preview table and
// recreate it then sort using the created at field in the preview table instead
@Entity
@Getter
@Setter
@Table(name = "medicine_preview")
public class MedicinePreview {
    @JoinColumn(name = "file_metadata_id", referencedColumnName = "id")
    private FileMetadata fileMetadata;
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    private Medicine medicine;
}
