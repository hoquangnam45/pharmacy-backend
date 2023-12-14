package com.hoquangnam45.pharmacy.pojo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class UpdateListingRequest {
    private final UUID listingId;
    private final BigDecimal price;
    private final Boolean disable;
    private final String uploadedSessionId;
}
