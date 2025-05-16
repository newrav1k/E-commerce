package ru.newrav1k.github.orderservice.model.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateItemRequest(
        @NotNull UUID orderId,
        @NotNull UUID productId,
        @Min(1) Integer quantity,
        @Positive @Digits(integer = 10, fraction = 2) BigDecimal price
) {

}