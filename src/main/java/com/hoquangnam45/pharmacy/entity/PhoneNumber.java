package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
@Setter
@Entity
@Table(name = "phone_number")
public class PhoneNumber {
    @Id
    @GeneratedValue
    private UUID id;
    private String countryCode;
    private String phoneNumber;

    @OneToOne(mappedBy = "phoneNumber")
    private User user;

    @OneToMany(mappedBy = "phoneNumber")
    private Set<DeliveryInfo> deliveryInfos;

    @OneToMany(mappedBy = "phoneNumber")
    private Set<DeliveryInfoAudit> deliveryInfoAudits;
}
