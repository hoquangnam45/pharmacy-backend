package com.hoquangnam45.pharmacy.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@Builder
public class MomoCaptureWalletRequestItem {
    private final UUID id;
    private final String name;
    private final String description;
    private final String category;
    private final String imageUrl;
    private final String manufacturer;
    private final Long price;
    private final String currency = "VND";
    private final Integer quantity;
    private final String unit;
    private final Long totalPrice;
    private final Long taxAmount;
}
