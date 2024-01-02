package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class MedicineListing {
    private final UUID id;
    private final PackagingUnit packagingUnit;
    private final String packagingUnitFriendlyName;
    private final BigDecimal price;
}
