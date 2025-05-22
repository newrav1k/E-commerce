package ru.newrav1k.mirea.core.model.event;

import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @param eventId
 * @param orderId
 * @param customerId
 * @param products
 * @param total
 */
public record SagaOrderCreationEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        List<ItemInformation> products,
        BigDecimal total
) {

}