package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.component.OrderMapper;
import com.hoquangnam45.pharmacy.entity.Order;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.pojo.OrderCreationResponse;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderRequest;
import com.hoquangnam45.pharmacy.service.MedicineService;
import com.hoquangnam45.pharmacy.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("order")
@Transactional
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;
    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    public ResponseEntity<OrderCreationResponse> placingOrder(@RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(orderMapper.createNewOrder(orderService.createNewOrder(request)));
    }

    @PostMapping
    public ResponseEntity<GenericResponse> updateOrderQuantity() {
        return null;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> updateOrderDeliveryInfo() {
        return null;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> updateOrderChosenListing() {
        return null;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> transactOrder() {
        return null;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> cancelOrder() {
        return null;
    }

    @PostMapping
    public ResponseEntity<List<?>> getAllOrderBelongToAccount() {
        return null;
    }

    @PostMapping
    public ResponseEntity<List<?>> getSpecificOrder() {
        return null;
    }
}
