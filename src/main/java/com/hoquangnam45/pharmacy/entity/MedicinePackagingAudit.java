package com.hoquangnam45.pharmacy.entity;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "medicine_packaging_audit_x")
public class MedicinePackagingAudit {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    private MedicineAudit medicine;

    @Enumerated(EnumType.STRING)
    private PackagingUnit packagingUnit;
    private Integer conversionFactor;
    private String conversionFactorDetail;

    @OneToOne(mappedBy = "packaging")
    private MedicineListingAudit listing;

    @Embedded
    private AuditInfo auditInfo;
}
