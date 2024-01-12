package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "delivery_info")
public class DeliveryInfo {
    @Id
    @GeneratedValue
    private UUID id;
    private String country;
    private String province;
    private String district;
    private String subDistrict;
    private String address;
    private String zipCode;

    @ManyToOne
    @JoinColumn(name = "phone_number_id")
    private PhoneNumber phoneNumber;

    // The reason for this is to allow them to have different recipient with 1 account
    private String recipientName;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
