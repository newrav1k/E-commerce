package ru.newrav1k.github.orderservice.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPayload(UUID id,
                          OrderPayload order,
                          UUID productId,
                          Integer quantity,
                          BigDecimal price) {

}