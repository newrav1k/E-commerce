package ru.mirea.newrav1k.productservice.model.dto;

import java.util.UUID;

public record InventoryResponse(UUID id,
                                UUID productId,
                                Integer quantity,
                                Integer reservedQuantity) {

}