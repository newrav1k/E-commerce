package ru.mirea.newrav1k.productservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.mirea.newrav1k.productservice.model.dto.InventoryPayload;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;
import ru.mirea.newrav1k.productservice.service.InventoryService;
import ru.newrav1k.mirea.core.model.event.SagaOrderCreatedEvent;

@Slf4j
@Component
@KafkaListener(topics = {
        "${product-service.kafka.topics.orders-event}",
        "${product-service.kafka.topics.products-event}"
}, groupId = "product-service")
@RequiredArgsConstructor
public class ProductCommandConsumer {

    private final InventoryService inventoryService;

    @KafkaHandler
    public void processCreationOrder(@Payload SagaOrderCreatedEvent event) {
        log.info("Received SagaOrderCreatedEvent: {}", event);
        for (var productId : event.productIds()) {
            Inventory inventory = this.inventoryService.findInventoryByProductId(productId);
            log.info("Received Inventory: {}", inventory);
        }
    }

}