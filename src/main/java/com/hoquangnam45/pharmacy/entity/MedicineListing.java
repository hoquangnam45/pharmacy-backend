package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "listing")
public class MedicineListing {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "medicine_packaging_id", referencedColumnName = "id")
    private MedicinePackaging packaging;

    private BigDecimal price;

    private boolean disable;

    @OneToMany(mappedBy = "listing")
    private Set<CartItem> cartItems;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
