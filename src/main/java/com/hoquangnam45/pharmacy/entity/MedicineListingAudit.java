package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "listing_audit")
public class MedicineListingAudit {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medicine_packaging_id", referencedColumnName = "id")
    private MedicinePackagingAudit packaging;

    private BigDecimal price;

    @OneToMany(mappedBy = "listing")
    private Set<Order> placedOrders;

    @Embedded
    private AuditInfo auditInfo;
}
