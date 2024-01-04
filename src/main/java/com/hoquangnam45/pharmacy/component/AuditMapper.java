package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.DeliveryInfo;
import com.hoquangnam45.pharmacy.entity.DeliveryInfoAudit;
import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicineAudit;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicineListingAudit;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.MedicinePackagingAudit;
import com.hoquangnam45.pharmacy.entity.MedicinePreview;
import com.hoquangnam45.pharmacy.entity.MedicinePreviewAudit;
import com.hoquangnam45.pharmacy.entity.Producer;
import com.hoquangnam45.pharmacy.entity.ProducerAudit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AuditMapper {
    @Mapping(target = "placedOrders", ignore = true)
    @Mapping(target = "auditInfo", ignore = true)
    @Mapping(target = "id", ignore = true)
    DeliveryInfoAudit createDeliveryInfoAudit(DeliveryInfo deliveryInfo);

    @Mapping(target = "placedOrders", ignore = true)
    @Mapping(target = "auditInfo", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "packaging", ignore = true)
    MedicineListingAudit createMedicineListingAudit(MedicineListing medicationListing);

    @Mapping(target = "auditInfo", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    MedicinePackagingAudit createMedicinePackagingAudit(MedicinePackaging medicinePackaging);

    @Mapping(target = "auditInfo", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "producer", ignore = true)
    @Mapping(target = "previews", ignore = true)
    MedicineAudit createMedicineAudit(Medicine medicine);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "medicine", ignore = true)
    MedicinePreviewAudit createMedicinePreviewAudit(MedicinePreview medicinePreview);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "auditInfo", ignore = true)
    ProducerAudit createProducerAudit(Producer producer);
}
