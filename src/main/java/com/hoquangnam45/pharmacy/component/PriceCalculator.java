package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.CartItem;
import com.hoquangnam45.pharmacy.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PriceCalculator {
    public BigDecimal calculateOrderPrice(Order order) {
        return order.getOrderItems().stream()
                .map(orderItem -> orderItem.getListing().getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateCartItemPrice(CartItem cartItem) {
        return BigDecimal.valueOf(cartItem.getQuantity()).multiply(cartItem.getListing().getPrice());
    }
}
