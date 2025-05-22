package ru.newrav1k.mirea.core.model.event;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @param eventId
 * @param orderId
 * @param customerId
 * @param total
 */
public record SagaPaymentSuccessEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        BigDecimal total
) {

}