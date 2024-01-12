package com.hoquangnam45.pharmacy.controller;

import com.hoquangnam45.pharmacy.component.mapper.OrderMapper;
import com.hoquangnam45.pharmacy.pojo.GenericResponse;
import com.hoquangnam45.pharmacy.pojo.OrderCreationResponse;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderCartRequest;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderRequest;
import com.hoquangnam45.pharmacy.pojo.UpdateOrderDeliveryInfoPayment;
import com.hoquangnam45.pharmacy.pojo.UpdateOrderDeliveryInfoRequest;
import com.hoquangnam45.pharmacy.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
        return ResponseEntity.ok(orderMapper.mapToNewOrderResponse(orderService.createNewOrder(request)));
    }

    @PostMapping("cart")
    public ResponseEntity<OrderCreationResponse> placingOrderCart(@RequestBody PlaceOrderCartRequest request) {
        return ResponseEntity.ok(orderMapper.mapToNewOrderResponse(orderService.createNewOrderCart(request)));
    }

    @PutMapping("{orderId}/delivery")
    public ResponseEntity<GenericResponse> updateOrderDeliveryInfo(
            @PathVariable("orderId") UUID orderId,
            @RequestBody UpdateOrderDeliveryInfoRequest request,
            HttpServletRequest servletRequest) {
        String path = servletRequest.getServletPath();
        orderService.updateOrderDeliveryInfo(orderId, request);
        return ResponseEntity.ok(new GenericResponse(200, path, "Update successfully", request.getDeliveryInfoId()));
    }

    @PutMapping("{orderId}/payment")
    public ResponseEntity<GenericResponse> updateOrderPaymentMethod(
            @PathVariable("orderId") UUID orderId,
            @RequestBody UpdateOrderDeliveryInfoPayment request,
            HttpServletRequest servletRequest) {
        String path = servletRequest.getServletPath();
        orderService.updateOrderPaymentMethod(orderId, request);
        return ResponseEntity.ok(new GenericResponse(200, path, "Update successfully", request.getPaymentId()));
    }

    @PostMapping("{orderId}/cancel")
    public ResponseEntity<GenericResponse> cancelOrder(
            @PathVariable("orderId") UUID orderId,
            HttpServletRequest servletRequest) {
        String path = servletRequest.getServletPath();
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok(new GenericResponse(200, path, "Cancel order successfully", orderId));
    }

    // This may involve a redirection to a third-party website, some kind of listener info need to be stored on db to know how to
    // handle when callback is received from the third party. The main processing should be happened when the callback is received,
    // the listener should have some kind of expired time so that if listener is expired it will not handle the callback
    // A job is also needed to move the state of transaction and order after an unsuccessful transaction due to expired listener
    @PostMapping("{orderId}/transact")
    public ResponseEntity<GenericResponse> transactOrder(
            @PathVariable("orderId") UUID orderId,
            HttpServletRequest servletRequest) {
        String path = servletRequest.getServletPath();
        orderService.transactOrder(orderId);
        return null;
    }
}
