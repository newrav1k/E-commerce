package ru.newrav1k.github.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.newrav1k.github.orderservice.model.dto.OrderResponse;
import ru.newrav1k.github.orderservice.model.entity.Order;
import ru.newrav1k.github.orderservice.model.dto.OrderPayload;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    Order toOrder(OrderPayload payload);

    Order toOrder(OrderResponse response);

    OrderPayload toOrderPayload(Order order);

    OrderResponse toOrderResponse(Order order);

}