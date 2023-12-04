package com.hoquangnam45.pharmacy.service;

import com.hoquangnam45.pharmacy.pojo.PlaceOrderRequest;
import com.hoquangnam45.pharmacy.repo.OrderRepo;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepo orderRepo;

    public OrderService(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    public void createNewOrder(PlaceOrderRequest placeOrderRequest) {

    }
}
