package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Embeddable
@Getter
@Setter
@Builder
public class AuditInfo {
    protected String auditObjectId;
    protected OffsetDateTime createdAt;
    protected boolean active;
}
