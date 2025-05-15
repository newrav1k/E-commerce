package ru.newrav1k.mirea.core.model.payload;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPayload(UUID id,
                          UUID productId,
                          Integer quantity,
                          BigDecimal price) {

}