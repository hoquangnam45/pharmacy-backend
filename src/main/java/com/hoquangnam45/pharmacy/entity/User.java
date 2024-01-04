package com.hoquangnam45.pharmacy.entity;

import com.hoquangnam45.pharmacy.constant.PermissionType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_info")
public class User {
    @Id
    @GeneratedValue
    private UUID id;
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;

    @OneToOne
    @JoinColumn(name = "phone_id", referencedColumnName = "id")
    private PhoneNumber phoneNumber;

    private boolean emailConfirmed;
    private boolean phoneNumberConfirmed;

    @OneToMany(mappedBy = "user")
    private Set<UserPermission> permissions;

    @OneToMany(mappedBy = "user")
    private Set<RefreshToken> refreshTokens;

    @OneToMany(mappedBy = "user")
    private Set<DeliveryInfo> deliveryAddresses;

    @OneToMany(mappedBy = "user")
    private Set<DeliveryInfoAudit> auditDeliveryAddresses;

    @OneToMany(mappedBy = "user")
    private Set<Order> placedOrders;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private Set<PaymentInfo> paymentInfos;
}
