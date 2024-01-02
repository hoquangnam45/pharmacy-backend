package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.MedicineListingAudit;
import com.hoquangnam45.pharmacy.entity.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class OrderPriceCalculator {
    public BigDecimal calculateOrderPrice(Order order) {
        return Optional.ofNullable(order.getListing())
                .map(MedicineListingAudit::getPrice)
                .flatMap(price -> Optional.ofNullable(order.getQuantity())
                        .map(BigDecimal::valueOf)
                        .map(quantity -> quantity.multiply(price))
                ).orElseThrow(() -> new UnsupportedOperationException("Order missing quantity or listing price, final price cannot be calculated"));
    }
}
