package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
public class PlaceOrderRequestItem {
    private final UUID listingId;
    private final Integer quantity;
}
