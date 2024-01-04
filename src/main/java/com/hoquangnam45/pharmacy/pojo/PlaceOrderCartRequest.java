package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class PlaceOrderCartRequest {
    private final List<UUID> cartItems;
    private final UUID deliveryInfoId;
    private final UUID paymentId;
}
