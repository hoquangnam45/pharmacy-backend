package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.component.mapper.AuditMapper;
import com.hoquangnam45.pharmacy.component.mapper.OrderMapper;
import com.hoquangnam45.pharmacy.component.PriceCalculator;
import com.hoquangnam45.pharmacy.constant.OrderStatus;
import com.hoquangnam45.pharmacy.constant.TransactionStatus;
import com.hoquangnam45.pharmacy.entity.AuditInfo;
import com.hoquangnam45.pharmacy.entity.CartItem;
import com.hoquangnam45.pharmacy.entity.DeliveryInfo;
import com.hoquangnam45.pharmacy.entity.DeliveryInfoAudit;
import com.hoquangnam45.pharmacy.entity.Medicine;
import com.hoquangnam45.pharmacy.entity.MedicineAudit;
import com.hoquangnam45.pharmacy.entity.MedicineListing;
import com.hoquangnam45.pharmacy.entity.MedicineListingAudit;
import com.hoquangnam45.pharmacy.entity.MedicinePackaging;
import com.hoquangnam45.pharmacy.entity.MedicinePackagingAudit;
import com.hoquangnam45.pharmacy.entity.Order;
import com.hoquangnam45.pharmacy.entity.OrderItem;
import com.hoquangnam45.pharmacy.entity.PaymentInfo;
import com.hoquangnam45.pharmacy.entity.Producer;
import com.hoquangnam45.pharmacy.entity.ProducerAudit;
import com.hoquangnam45.pharmacy.entity.TransactionInfo;
import com.hoquangnam45.pharmacy.exception.ApiError;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderCartRequest;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderRequest;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderRequestItem;
import com.hoquangnam45.pharmacy.pojo.UpdateOrderDeliveryInfoPayment;
import com.hoquangnam45.pharmacy.pojo.UpdateOrderDeliveryInfoRequest;
import com.hoquangnam45.pharmacy.repo.CartItemRepo;
import com.hoquangnam45.pharmacy.repo.DeliveryInfoRepo;
import com.hoquangnam45.pharmacy.repo.MedicineListingRepo;
import com.hoquangnam45.pharmacy.repo.OrderItemRepo;
import com.hoquangnam45.pharmacy.repo.OrderRepo;
import com.hoquangnam45.pharmacy.repo.PaymentRepo;
import com.hoquangnam45.pharmacy.repo.TransactionInfoRepo;
import com.hoquangnam45.pharmacy.repo.UserRepo;
import com.hoquangnam45.pharmacy.repo.audit.DeliveryInfoAuditRepo;
import com.hoquangnam45.pharmacy.repo.audit.MedicineAuditRepo;
import com.hoquangnam45.pharmacy.repo.audit.MedicineListingAuditRepo;
import com.hoquangnam45.pharmacy.repo.audit.MedicinePackagingAuditRepo;
import com.hoquangnam45.pharmacy.repo.audit.ProducerAuditRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final MedicineAuditRepo medicineAuditRepo;
    private final MedicinePackagingAuditRepo medicinePackagingAuditRepo;
    private final MedicineListingRepo medicineListingRepo;
    private final MedicineListingAuditRepo medicineListingAuditRepo;
    private final DeliveryInfoAuditRepo deliveryInfoAuditRepo;
    private final DeliveryInfoRepo deliveryInfoRepo;
    private final PriceCalculator orderPriceCalculator;
    private final PaymentRepo paymentRepo;
    private final TransactionInfoRepo transactionInfoRepo;
    private final ProducerAuditRepo producerAuditRepo;
    private final AuditMapper auditMapper;
    private final OrderItemRepo orderItemRepo;
    private final CartItemRepo cartItemRepo;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepo orderRepo, UserRepo userRepo, MedicineAuditRepo medicineAuditRepo, MedicinePackagingAuditRepo medicinePackagingAuditRepo, MedicineListingRepo medicineListingRepo, MedicineListingAuditRepo medicineListingAuditRepo, DeliveryInfoAuditRepo deliveryInfoAuditRepo, DeliveryInfoRepo deliveryInfoRepo, PriceCalculator orderPriceCalculator, PaymentRepo paymentRepo, TransactionInfoRepo transactionInfoRepo, ProducerAuditRepo producerAuditRepo, AuditMapper auditMapper, OrderItemRepo orderItemRepo, CartItemRepo cartItemRepo, OrderMapper orderMapper) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.medicineAuditRepo = medicineAuditRepo;
        this.medicinePackagingAuditRepo = medicinePackagingAuditRepo;
        this.medicineListingRepo = medicineListingRepo;
        this.medicineListingAuditRepo = medicineListingAuditRepo;
        this.deliveryInfoAuditRepo = deliveryInfoAuditRepo;
        this.deliveryInfoRepo = deliveryInfoRepo;
        this.orderPriceCalculator = orderPriceCalculator;
        this.paymentRepo = paymentRepo;
        this.transactionInfoRepo = transactionInfoRepo;
        this.producerAuditRepo = producerAuditRepo;
        this.auditMapper = auditMapper;
        this.orderItemRepo = orderItemRepo;
        this.cartItemRepo = cartItemRepo;
        this.orderMapper = orderMapper;
    }

    public Order createNewOrderCart(PlaceOrderCartRequest request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        List<CartItem> cartItems = cartItemRepo.findAllByCart_User_IdAndIdIn(userId, request.getCartItems());
        PlaceOrderRequest placeOrderRequest = PlaceOrderRequest.builder()
                .orderItems(cartItems.stream().map(cartItem -> PlaceOrderRequestItem.builder()
                                .listingId(cartItem.getListingId())
                                .quantity(cartItem.getQuantity())
                                .build()).collect(Collectors.toList()))
                .deliveryInfoId(request.getDeliveryInfoId())
                .paymentId(request.getPaymentId())
                .build();

        // Clear cart items after order has been created from this cart items
        cartItemRepo.deleteAllByCart_User_IdAndIdIn(userId, request.getCartItems());
        return createNewOrder(placeOrderRequest);
    }

    // Copy all info needed for order to a separate table so that even if the info in the db is changed, it will stay the same for order
    public Order createNewOrder(PlaceOrderRequest placeOrderRequest) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        DeliveryInfo deliveryInfo = Optional.ofNullable(deliveryInfoRepo.findByUser_IdAndId(userId, placeOrderRequest.getDeliveryInfoId()))
                .orElseThrow(() -> new ApiError(404, "Not found delivery info"));
        OffsetDateTime deliveryInfoLastUpdatedAt = Optional.ofNullable(deliveryInfo.getUpdatedAt())
                .orElseGet(deliveryInfo::getCreatedAt);
        DeliveryInfoAudit deliveryInfoAudit = deliveryInfoAuditRepo.findByAuditInfo_AuditObjectIdAndAuditInfo_CreatedAt(placeOrderRequest.getDeliveryInfoId().toString(), deliveryInfoLastUpdatedAt);
        if (deliveryInfoAudit == null) {
            deliveryInfoAuditRepo.disableAllActiveAuditObject(deliveryInfo.getId().toString());
            deliveryInfoAudit = deliveryInfoAuditRepo.save(createAuditDeliveryInfo(deliveryInfo, deliveryInfoLastUpdatedAt));
        }
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Order newOrder = orderRepo.save(Order.builder()
                .createdAt(now)
                .status(OrderStatus.NEW)
                .user(userRepo.getReferenceById(userId))
                .deliveryInfo(deliveryInfoAudit)
                .build());
        Map<UUID, MedicineListing> medicineListings = medicineListingRepo.findAllByIdIn(placeOrderRequest.getOrderItems().stream().map(PlaceOrderRequestItem::getListingId).collect(Collectors.toSet())).stream()
                .collect(Collectors.toMap(
                        MedicineListing::getId,
                        Function.identity()));
        if (medicineListings.size() != placeOrderRequest.getOrderItems().size()) {
            throw new ApiError(404, "Not found listing");
        }
        newOrder.setOrderItems(placeOrderRequest.getOrderItems().stream().map(orderItem -> {
            MedicineListing medicineListing = Optional.ofNullable(medicineListings.get(orderItem.getListingId()))
                    .orElseThrow(() -> new ApiError(404, "Not found listing"));
            OffsetDateTime medicineListingLastUpdatedAt = Optional.ofNullable(medicineListing.getUpdatedAt())
                    .orElseGet(deliveryInfo::getCreatedAt);
            MedicineListingAudit medicineListingAudit = medicineListingAuditRepo.findByAuditInfo_AuditObjectIdAndAuditInfo_CreatedAt(medicineListing.getId().toString(), medicineListingLastUpdatedAt);
            if (medicineListingAudit == null) {
                medicineListingAuditRepo.disableAllActiveAuditObject(medicineListing.getId().toString());
                medicineListingAudit = medicineListingAuditRepo.save(createAuditMedicineListing(medicineListing, medicineListingLastUpdatedAt));
            }
            return OrderItem.builder()
                    .listing(medicineListingAudit)
                    .quantity(orderItem.getQuantity())
                    .order(newOrder)
                    .build();
        }).map(orderItemRepo::save).collect(Collectors.toSet()));

        PaymentInfo payment = Optional.ofNullable(paymentRepo.findByUser_IdAndId(userId, placeOrderRequest.getPaymentId()))
                .orElseThrow(() -> new ApiError(404, "Not found payment info"));
        newOrder.setTransactionInfo(transactionInfoRepo.save(TransactionInfo.builder()
                .order(newOrder)
                .amount(orderPriceCalculator.calculateOrderPrice(newOrder))
                .createdAt(now)
                .payment(payment)
                .status(TransactionStatus.PENDING)
                .build()));

        return orderRepo.save(newOrder);
    }

    private DeliveryInfoAudit createAuditDeliveryInfo(DeliveryInfo deliveryInfo, OffsetDateTime lastUpdatedAt) {
        DeliveryInfoAudit deliveryInfoAudit = auditMapper.createDeliveryInfoAudit(deliveryInfo);
        deliveryInfoAudit.setAuditInfo(AuditInfo.builder()
                .auditObjectId(deliveryInfo.getId().toString())
                .active(true)
                .createdAt(lastUpdatedAt)
                .build());
        return deliveryInfoAudit;
    }

    private MedicineListingAudit createAuditMedicineListing(MedicineListing medicineListing, OffsetDateTime lastUpdatedAt) {
        MedicineListingAudit deliveryInfoAudit = auditMapper.createMedicineListingAudit(medicineListing);
        deliveryInfoAudit.setAuditInfo(AuditInfo.builder()
                .auditObjectId(medicineListing.getId().toString())
                .active(true)
                .createdAt(lastUpdatedAt)
                .build());
        OffsetDateTime lastUpdatedAtMedicinePackaging = Optional.ofNullable(medicineListing.getPackaging().getUpdatedAt())
                .orElseGet(medicineListing.getPackaging()::getCreatedAt);
        MedicinePackagingAudit packagingAudit = medicinePackagingAuditRepo.findByAuditInfo_AuditObjectIdAndAuditInfo_CreatedAt(medicineListing.getPackaging().getId().toString(), lastUpdatedAtMedicinePackaging);
        if (packagingAudit == null) {
            medicinePackagingAuditRepo.disableAllActiveAuditObject(medicineListing.getPackaging().getId().toString());
            packagingAudit = medicinePackagingAuditRepo.save(createAuditMedicinePackaging(medicineListing.getPackaging(), lastUpdatedAtMedicinePackaging));
        }
        deliveryInfoAudit.setPackaging(packagingAudit);
        return deliveryInfoAudit;
    }

    private MedicinePackagingAudit createAuditMedicinePackaging(MedicinePackaging medicinePackaging, OffsetDateTime lastUpdatedAt) {
        MedicinePackagingAudit packagingAudit = auditMapper.createMedicinePackagingAudit(medicinePackaging);
        packagingAudit.setAuditInfo(AuditInfo.builder()
                .auditObjectId(medicinePackaging.getId().toString())
                .active(true)
                .createdAt(lastUpdatedAt)
                .build());
        OffsetDateTime lastUpdatedAtMedicine = Optional.ofNullable(medicinePackaging.getMedicine().getUpdatedAt())
                .orElseGet(medicinePackaging.getMedicine()::getCreatedAt);
        MedicineAudit medicineAudit = medicineAuditRepo.findByAuditInfo_AuditObjectIdAndAuditInfo_CreatedAt(medicinePackaging.getMedicine().getId().toString(), lastUpdatedAtMedicine);
        if (medicineAudit == null) {
            medicineAuditRepo.disableAllActiveAuditObject(medicinePackaging.getMedicine().getId().toString());
            medicineAudit = medicineAuditRepo.save(createAuditMedicine(medicinePackaging.getMedicine(), lastUpdatedAtMedicine));
        }
        packagingAudit.setMedicine(medicineAudit);
        return packagingAudit;
    }

    private MedicineAudit createAuditMedicine(Medicine medicine, OffsetDateTime lastUpdatedAt) {
        MedicineAudit medicineAudit = auditMapper.createMedicineAudit(medicine);
        medicineAudit.setAuditInfo(AuditInfo.builder()
                .auditObjectId(medicine.getId().toString())
                .active(true)
                .createdAt(lastUpdatedAt)
                .build());
        OffsetDateTime lastUpdatedAtProducer = Optional.ofNullable(medicine.getProducer().getUpdatedAt())
                .orElseGet(medicine.getProducer()::getCreatedAt);
        ProducerAudit producerAudit = producerAuditRepo.findByAuditInfo_AuditObjectIdAndAuditInfo_CreatedAt(medicine.getProducer().getId().toString(), lastUpdatedAtProducer);
        if (producerAudit == null) {
            producerAuditRepo.disableAllActiveAuditObject(medicine.getProducer().getId().toString());
            producerAudit = producerAuditRepo.save(createAuditProducer(medicine.getProducer(), lastUpdatedAtProducer));
        }
        medicineAudit.setTags(medicine.getTags());
        medicineAudit.setPreviews(medicine.getPreviews().stream().map(auditMapper::createMedicinePreviewAudit).collect(Collectors.toSet()));
        medicineAudit.setProducer(producerAudit);
        return medicineAudit;
    }

    private ProducerAudit createAuditProducer(Producer producer, OffsetDateTime lastUpdatedAt) {
        ProducerAudit producerAudit = auditMapper.createProducerAudit(producer);
        producerAudit.setAuditInfo(AuditInfo.builder()
                .auditObjectId(producer.getId().toString())
                .active(true)
                .createdAt(lastUpdatedAt)
                .build());
        return producerAudit;
    }

    public void updateOrderDeliveryInfo(UUID orderId, UpdateOrderDeliveryInfoRequest request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Order order = Optional.ofNullable(orderRepo.findByUser_IdAndId(userId, orderId))
                .orElseThrow(() -> new ApiError(404, "Order not found"));
        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException("Order is already locked and cannot be updated further anymore");
        }
        DeliveryInfo deliveryInfo = Optional.ofNullable(deliveryInfoRepo.findByUser_IdAndId(userId, request.getDeliveryInfoId()))
                .orElseThrow(() -> new ApiError(404, "Delivery not found"));
        OffsetDateTime lastUpdatedAtDeliveryInfo = Optional.ofNullable(deliveryInfo.getUpdatedAt())
                .orElseGet(deliveryInfo::getCreatedAt);
        DeliveryInfoAudit deliveryInfoAudit = deliveryInfoAuditRepo.findByAuditInfo_AuditObjectIdAndAuditInfo_CreatedAt(
                deliveryInfo.getId().toString(), lastUpdatedAtDeliveryInfo);
        if (deliveryInfoAudit == null) {
            deliveryInfoAuditRepo.disableAllActiveAuditObject(deliveryInfo.getId().toString());
            deliveryInfoAudit = deliveryInfoAuditRepo.save(createAuditDeliveryInfo(deliveryInfo, lastUpdatedAtDeliveryInfo));
        }
        order.setDeliveryInfo(deliveryInfoAudit);
    }

    public void updateOrderPaymentMethod(UUID orderId, UpdateOrderDeliveryInfoPayment request) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        Order order = Optional.ofNullable(orderRepo.findByUser_IdAndId(userId, orderId))
                .orElseThrow(() -> new ApiError(404, "Order not found"));
        if (order.getStatus() != OrderStatus.NEW) {
            throw new IllegalStateException("Order is already locked and cannot be updated further anymore");
        }
        PaymentInfo paymentInfo = Optional.ofNullable(paymentRepo.findByUser_IdAndId(userId, request.getPaymentId()))
                .orElseThrow(() -> new ApiError(404, "Delivery not found"));
        // Sanity-check
        TransactionInfo transactionInfo = order.getTransactionInfo();
        if (transactionInfo.getStatus() != TransactionStatus.PENDING) {
            // This should not happen if all the state handling is correct
            throw new IllegalStateException("Transaction state for order is not valid");
        }
        transactionInfo.setPayment(paymentInfo);
    }

    // This should also remove any listeners for transaction that were created to handle third-party callback
    public void cancelOrder(UUID orderId) {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());

    }

    public void transactOrder(UUID orderId) {
    }
}
