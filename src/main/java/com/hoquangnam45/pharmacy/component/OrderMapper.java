package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.Order;
import com.hoquangnam45.pharmacy.pojo.OrderCreationResponse;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderCartRequest;
import com.hoquangnam45.pharmacy.pojo.PlaceOrderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderMapper {
    OrderCreationResponse mapToNewOrderResponse(Order order);
}
