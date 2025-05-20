package ru.newrav1k.mirea.core.model.event;

import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record SagaCreationCancelledEvent(
        UUID orderId,
        UUID customerId,
        List<ItemInformation> products,
        BigDecimal total
) {

}