package ru.mirea.newrav1k.productservice.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(
        @NotNull @Size(min = 3, max = 50) String name,
        @NotNull String description,
        @Positive @Digits(integer = 10, fraction = 2) BigDecimal price,
        @Valid List<InitialInventoryRequest> inventories
) {

    public record InitialInventoryRequest(
            @Min(0) Integer quantity,
            @Min(0) Integer reservedQuantity
    ) {

    }

}