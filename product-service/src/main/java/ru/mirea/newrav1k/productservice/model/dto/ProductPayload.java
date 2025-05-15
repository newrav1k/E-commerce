package ru.mirea.newrav1k.productservice.model.dto;

import ru.mirea.newrav1k.productservice.model.enums.ProductStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductPayload(UUID id,
                             String name,
                             String description,
                             ProductStatus status,
                             BigDecimal price) {

}