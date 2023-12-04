package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class AuditInfo {
    protected String auditObjectId;
    @CreationTimestamp
    protected OffsetDateTime createdAt;
    protected boolean active;
}