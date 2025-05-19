package ru.newrav1k.mirea.core.model.payload;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(UUID id,
                              String name,
                              String description,
                              String status,
                              BigDecimal price) {

}