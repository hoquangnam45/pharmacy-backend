package com.hoquangnam45.pharmacy.entity;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "medicine_packaging_x")
@Builder
public class MedicinePackaging {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medicine_id", referencedColumnName = "id")
    private Medicine medicine;

    @Enumerated(EnumType.STRING)
    private PackagingUnit packagingUnit;
    private Integer conversionFactor;
    private String conversionFactorDetail;

    @OneToMany(mappedBy = "packaging")
    private Set<MedicineListing> listings;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime deletedAt;
}
