package ru.newrav1k.mirea.core.model.event;

import java.util.UUID;

public record SagaProductReservationFailedEvent(UUID orderId,
                                                String reason) {

}