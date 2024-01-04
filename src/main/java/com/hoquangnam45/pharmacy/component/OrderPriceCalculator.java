package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.MedicineListingAudit;
import com.hoquangnam45.pharmacy.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class OrderPriceCalculator {
    public BigDecimal calculateOrderPrice(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getListing().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
