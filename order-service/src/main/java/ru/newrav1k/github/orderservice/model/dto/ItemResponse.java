package ru.newrav1k.github.orderservice.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemResponse(UUID id,
                           UUID productId,
                           Integer quantity,
                           BigDecimal price) {

}