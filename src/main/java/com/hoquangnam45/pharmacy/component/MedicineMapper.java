package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.Producer;
import com.hoquangnam45.pharmacy.pojo.MedicineDetailCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicineListingCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicinePackagingRequest;
import com.hoquangnam45.pharmacy.pojo.ProducerCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MedicineMapper {
    @Mapping(target = "previews", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producer", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "allowPackagingUnits", ignore = true)
    Medicine createMedicine(MedicineDetailCreateRequest createRequest);

    @Mapping(target = "medicines", ignore = true)
    @Mapping(target = "id", ignore = true)
    Producer createProducer(ProducerCreateRequest createRequest);

    @Mapping(target = "disable", ignore = true)
    @Mapping(target = "packaging", ignore = true)
    @Mapping(target = "id", ignore = true)
    MedicineListing createMedicineListing(MedicineListingCreateRequest createRequest);

    @Mapping(target = "listings", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    @Mapping(target = "id", ignore = true)
    MedicinePackaging createMedicinePackaging(MedicinePackagingRequest createRequest);
}
