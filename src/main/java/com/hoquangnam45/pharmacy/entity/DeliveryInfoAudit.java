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
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "delivery_info_audit")
public class DeliveryInfoAudit {
    @Id
    @GeneratedValue
    private UUID id;
    private String country;
    private String province;
    private String district;
    private String subDistrict;
    private String address;
    private String zipCode;

    // Why separate phone numbers here. because the phone numbers from db is for used with identify user
    // if linked  to the
    private String countryCode;
    private String phoneNumber;

    private String recipientName;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "deliveryInfo")
    private Set<Order> placedOrders;

    @Embedded
    private AuditInfo auditInfo;
}
