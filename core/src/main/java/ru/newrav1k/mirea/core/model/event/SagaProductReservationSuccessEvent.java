package ru.newrav1k.mirea.core.model.event;

import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * @param eventId
 * @param orderId
 * @param customerId
 * @param items
 * @param total
 */
public record SagaProductReservationSuccessEvent(
        UUID eventId,
        UUID orderId,
        UUID customerId,
        List<ItemInformation> items,
        BigDecimal total
) {

}