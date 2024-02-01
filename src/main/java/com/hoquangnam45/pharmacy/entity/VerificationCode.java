package com.hoquangnam45.pharmacy.entity;

import com.hoquangnam45.pharmacy.constant.VerificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "verification_code")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class VerificationCode {
    @Id
    @GeneratedValue
    private UUID id;

    private String verificationCode;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private VerificationType type;

    private OffsetDateTime lastSentAt;

    private OffsetDateTime expiredAt;
}
