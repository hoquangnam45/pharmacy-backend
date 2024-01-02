package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public final class PlaceOrderRequest {
    private final UUID listingId;
    private final Integer quantity;
    private final UUID deliveryInfoId;
    private final UUID paymentId;
}
