package com.hoquangnam45.pharmacy.pojo;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Builder
public class MedicineListingCreateRequest {
    private final UUID packagingId;
    private final BigDecimal price;
}
