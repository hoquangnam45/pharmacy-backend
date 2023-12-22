package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import com.hoquangnam45.pharmacy.constant.UsageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class MedicineDetailCreateRequest {
    private final String name;
    private final String description;
    private final UUID mainPreviewId;
    private final List<String> tags;
    private final List<String> activeIngredients;
    private final PackagingUnit basicUnit;
    private final List<MedicinePackagingRequest> allowPackagingUnits;
    private final String sideEffect;
    private final UUID producerId;
    private final UsageType usageType;
    private final UUID uploadSessionId;
}
