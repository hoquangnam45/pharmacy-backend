package com.hoquangnam45.pharmacy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "momo_payment_detail")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MomoPaymentDetail {
    private UUID id;

    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private PaymentInfo payment;

    private String momoPhoneNumber;

}
