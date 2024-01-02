package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.component.OrderPriceCalculator;
import com.hoquangnam45.pharmacy.constant.OrderStatus;
import com.hoquangnam45.pharmacy.constant.TransactionStatus;
import com.hoquangnam45.pharmacy.entity.DeliveryInfo;
import com.hoquangnam45.pharmacy.entity.DeliveryInfoAudit;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicineListingAudit;
import com.hoquangnam45.pharmacy.entity.Order;
import com.hoquangnam45.pharmacy.entity.PaymentInfo;
import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderRequest;
import com.hoquangnam45.pharmacy.repo.DeliveryInfoRepo;
import com.hoquangnam45.pharmacy.repo.MedicineListingRepo;
import com.hoquangnam45.pharmacy.repo.MedicineRepo;
import com.hoquangnam45.pharmacy.repo.OrderRepo;
import com.hoquangnam45.pharmacy.repo.PaymentRepo;
import com.hoquangnam45.pharmacy.repo.TransactionInfoRepo;
import com.hoquangnam45.pharmacy.repo.UserRepo;
import com.hoquangnam45.pharmacy.repo.audit.DeliveryInfoAuditRepo;
import com.hoquangnam45.pharmacy.repo.audit.MedicineAuditRepo;
import com.hoquangnam45.pharmacy.repo.audit.MedicineListingAuditRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final MedicineRepo medicineRepo;
    private final MedicineAuditRepo medicineAuditRepo;
    private final MedicineListingRepo medicineListingRepo;
    private final MedicineListingAuditRepo medicineListingAuditRepo;
    private final DeliveryInfoAuditRepo deliveryInfoAuditRepo;
    private final DeliveryInfoRepo deliveryInfoRepo;
    private final OrderPriceCalculator orderPriceCalculator;
    private final PaymentRepo paymentRepo;
    private final TransactionInfoRepo transactionInfoRepo;

    public OrderService(OrderRepo orderRepo, UserRepo userRepo, MedicineRepo medicineRepo, MedicineAuditRepo medicineAuditRepo, MedicineListingRepo medicineListingRepo, MedicineListingAuditRepo medicineListingAuditRepo, DeliveryInfoAuditRepo deliveryInfoAuditRepo, DeliveryInfoRepo deliveryInfoRepo, OrderPriceCalculator orderPriceCalculator, PaymentRepo paymentRepo, TransactionInfoRepo transactionInfoRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.medicineRepo = medicineRepo;
        this.medicineAuditRepo = medicineAuditRepo;
        this.medicineListingRepo = medicineListingRepo;
        this.medicineListingAuditRepo = medicineListingAuditRepo;
        this.deliveryInfoAuditRepo = deliveryInfoAuditRepo;
        this.deliveryInfoRepo = deliveryInfoRepo;
        this.orderPriceCalculator = orderPriceCalculator;
        this.paymentRepo = paymentRepo;
        this.transactionInfoRepo = transactionInfoRepo;
    }

    public Order createNewOrder(PlaceOrderRequest placeOrderRequest) {
        DeliveryInfoAudit deliveryInfoAudit = deliveryInfoAuditRepo.findByAuditInfo_AuditObjectId(placeOrderRequest.getDeliveryInfoId().toString());
        if (deliveryInfoAudit == null) {
            DeliveryInfo deliveryInfo = deliveryInfoRepo.findById(placeOrderRequest.getDeliveryInfoId()).orElseThrow(() -> new ApiError(404, "Not found delivery info"));
            deliveryInfoAudit = addAuditDeliveryInfo(deliveryInfo);
        }
        MedicineListingAudit medicineListingAudit = medicineListingAuditRepo.findByAuditInfo_AuditObjectId(placeOrderRequest.getListingId().toString());
        if (medicineListingAudit == null) {
            MedicineListing medicineListing = medicineListingRepo.findById(placeOrderRequest.getListingId()).orElseThrow(() -> new ApiError(404, "Not found listing"));
            medicineListingAudit = addAuditMedicineListing(medicineListing);
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Order newOrder = orderRepo.save(Order.builder()
                .createdAt(now)
                .status(OrderStatus.NEW)
                .listing(medicineListingAudit)
                .quantity(placeOrderRequest.getQuantity())
                .user(userRepo.getReferenceById(userId))
                .deliveryInfo(deliveryInfoAudit)
                .build());
        PaymentInfo payment = Optional.ofNullable(paymentRepo.findByUser_IdAndId(userId, placeOrderRequest.getPaymentId()))
                .orElseThrow(() -> new ApiError(404, "Not found payment info"));
        newOrder.setTransactionInfo(transactionInfoRepo.save(TransactionInfo.builder()
                .order(newOrder)
                .amount(orderPriceCalculator.calculateOrderPrice(newOrder))
                .createdAt(now)
                .payment(payment)
                .status(TransactionStatus.PENDING)
                .build()));
        return newOrder;
    }

    private DeliveryInfoAudit addAuditDeliveryInfo(DeliveryInfo deliveryInfo) {
    }

    private MedicineListingAudit addAuditMedicineListing(MedicineListing medicine) {

    }
}
