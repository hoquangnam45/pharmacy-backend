package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public final class PlaceOrderRequest {
    private final String medicineId;
    private final String listingId;
    private final String quantity;
}
