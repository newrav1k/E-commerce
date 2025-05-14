package ru.newrav1k.mirea.core.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private UUID orderId;

    private UUID customerId;

    private UUID productId;

    private Integer quantity;

}