package ru.newrav1k.mirea.core.model.event;

import java.math.BigDecimal;
import java.util.UUID;

public record SagaOrderConfirmedEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        BigDecimal total
) {

}