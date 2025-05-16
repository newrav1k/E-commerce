package ru.mirea.newrav1k.productservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;
import ru.mirea.newrav1k.productservice.service.InventoryService;
import ru.newrav1k.mirea.core.model.event.SagaOrderCreationEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationFailedEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationSuccessEvent;

@Slf4j
@Component
@KafkaListener(topics = {
        "${product-service.kafka.topics.order-created}"
}, groupId = "${product-service.kafka.group-id}")
@RequiredArgsConstructor
public class ProductCommandConsumer {

    @Value("${product-service.kafka.topics.product-reservation}")
    private String productReservationTopic;

    private final InventoryService inventoryService;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaHandler
    @Transactional(rollbackFor = {Exception.class})
    public void processCreationOrder(@Payload SagaOrderCreationEvent event) {
        log.info("Received SagaOrderCreatedEvent: {}", event);
        try {
            for (var product : event.products()) {
                Inventory inventory = this.inventoryService.findInventoryByProductId(product.productId());
                inventory.reserveQuantity(product.quantity());
            }
            SagaProductReservationSuccessEvent sagaProductReservationSuccessEvent = new SagaProductReservationSuccessEvent(event.orderId());
            this.kafkaTemplate.send(this.productReservationTopic, sagaProductReservationSuccessEvent);
        } catch (Exception exception) {
            log.error("Error while processing SagaOrderCreatedEvent", exception);
            SagaProductReservationFailedEvent sagaProductReservationFailedEvent = new SagaProductReservationFailedEvent(
                    event.orderId(),
                    exception.getMessage()
            );
            this.kafkaTemplate.send(this.productReservationTopic, sagaProductReservationFailedEvent);

            throw exception;
        }
    }

}