package com.hoquangnam45.pharmacy.component;

import com.hoquangnam45.pharmacy.entity.Order;
import com.hoquangnam45.pharmacy.pojo.OrderCreationResponse;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {
    OrderCreationResponse createNewOrder(Order order);
}
