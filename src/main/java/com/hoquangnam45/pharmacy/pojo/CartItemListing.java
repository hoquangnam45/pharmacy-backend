package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
public class CartItemListing {
    private final UUID id;
    private final PackagingUnit packagingUnit;
    private final UUID packagingId;
    private final UUID medicineId;
    private final BigDecimal price;
    private final String listingPreview;
    private final String description;
}
