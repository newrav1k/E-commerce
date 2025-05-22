package ru.newrav1k.mirea.core.model.event;

import java.util.UUID;

/**
 * @param eventId
 * @param orderId
 * @param customerId
 * @param reason
 */
public record SagaProductReservationFailedEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        String reason
) {

}