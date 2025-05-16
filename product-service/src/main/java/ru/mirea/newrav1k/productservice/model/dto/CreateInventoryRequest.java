package ru.mirea.newrav1k.productservice.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInventoryRequest(
        @NotNull UUID productId,
        @Min(0) Integer quantity,
        @Min(0) Integer reservedQuantity
) {

}