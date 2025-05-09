package ru.newrav1k.github.orderservice.web.dto;

import ru.newrav1k.github.orderservice.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderPayload(UUID id,
                           UUID userId,
                           OrderStatus status,
                           BigDecimal total) {

}