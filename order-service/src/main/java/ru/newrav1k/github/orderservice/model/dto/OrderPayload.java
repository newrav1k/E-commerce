package ru.newrav1k.github.orderservice.model.dto;

import ru.newrav1k.github.orderservice.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderPayload(UUID id,
                           UUID customerId,
                           OrderStatus status,
                           BigDecimal total,
                           List<ItemPayload> items) {

}