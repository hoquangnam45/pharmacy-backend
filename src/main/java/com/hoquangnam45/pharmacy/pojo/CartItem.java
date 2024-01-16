package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
public class CartItem {
    private final UUID id;
    private final int quantity;
    private final CartItemListing listing;
    private final BigDecimal price;
}
