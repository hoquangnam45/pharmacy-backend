package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class MedicinePackagingRequest {
    private final UUID medicineId;
    private final PackagingUnit packagingUnit;
    private final Integer conversionFactor;
    private final String conversionFactorDetail;
    private final MedicineListingCreateRequest listingCreateRequest;
}
