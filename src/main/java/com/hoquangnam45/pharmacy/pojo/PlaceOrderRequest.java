package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
public final class PlaceOrderRequest {
    private final List<PlaceOrderRequestItem> orderItems;
    private final UUID deliveryInfoId;
    private final UUID paymentId;
}
