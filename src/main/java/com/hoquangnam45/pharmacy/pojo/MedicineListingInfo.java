package com.hoquangnam45.pharmacy.pojo;

import com.hoquangnam45.pharmacy.constant.PackagingUnit;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class MedicineListingInfo {
    private final UUID id;
    private final String name;
    private final List<MedicineListing> listings;
    private final PackagingUnit defaultUnit;
}
