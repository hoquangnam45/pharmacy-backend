package com.hoquangnam45.pharmacy.component.mapper;

import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.Producer;
import com.hoquangnam45.pharmacy.pojo.FileMetadata;
import com.hoquangnam45.pharmacy.pojo.MedicineDetailCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicineListingCreateRequest;
import com.hoquangnam45.pharmacy.pojo.MedicinePackagingRequest;
import com.hoquangnam45.pharmacy.pojo.ProducerCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper
public interface MedicineMapper {
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "previews", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "producer", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "allowPackagingUnits", ignore = true)
    Medicine createMedicine(MedicineDetailCreateRequest createRequest);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "medicines", ignore = true)
    @Mapping(target = "id", ignore = true)
    Producer createProducer(ProducerCreateRequest createRequest);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "disable", ignore = true)
    @Mapping(target = "packaging", ignore = true)
    @Mapping(target = "id", ignore = true)
    MedicineListing createMedicineListing(MedicineListingCreateRequest createRequest);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "listings", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    @Mapping(target = "id", ignore = true)
    MedicinePackaging createMedicinePackaging(MedicinePackagingRequest createRequest);

    FileMetadata createResponseFileMetadata(com.hoquangnam45.pharmacy.entity.FileMetadata metadata, UUID uploadSessionId, UUID uploadSessionFileId, String downloadPath);
}
