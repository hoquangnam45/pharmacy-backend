package com.hoquangnam45.pharmacy.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;

@Builder
@AllArgsConstructor
@Getter
public class MedicineFilter {
    private final String name;
    private final String description;
    private final BigDecimal priceFrom;
    private final BigDecimal priceTo;
    private final Set<String> tags;
}
