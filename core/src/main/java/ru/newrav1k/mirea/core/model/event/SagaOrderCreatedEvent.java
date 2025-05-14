package ru.newrav1k.mirea.core.model.event;

import java.util.List;
import java.util.UUID;

public record SagaOrderCreatedEvent(UUID orderId,
                                    UUID customerId,
                                    List<UUID> productIds) {

}