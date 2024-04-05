package com.hoquangnam45.pharmacy.entity;

import com.hoquangnam45.pharmacy.constant.OrderStatus;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "order_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "delivery_info_id", referencedColumnName = "id")
    private DeliveryInfoAudit deliveryInfo;

    @OneToOne(mappedBy = "order")
    private TransactionInfo transactionInfo;

    @OneToMany(mappedBy = "order")
    private Set<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "listing_id", referencedColumnName = "id")
    private MedicineListingAudit listing;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
