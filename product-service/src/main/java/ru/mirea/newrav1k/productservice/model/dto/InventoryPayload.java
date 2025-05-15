package ru.mirea.newrav1k.productservice.model.dto;

import java.util.UUID;

public record InventoryPayload(UUID id,
                               UUID productId,
                               Integer quantity,
                               Integer reservedQuantity) {

}