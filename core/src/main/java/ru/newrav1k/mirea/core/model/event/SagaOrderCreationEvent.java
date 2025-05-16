package ru.newrav1k.mirea.core.model.event;

import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.util.List;
import java.util.UUID;

public record SagaOrderCreationEvent(UUID orderId,
                                    UUID customerId,
                                    List<ItemInformation> products) {

}