package ru.mirea.newrav1k.productservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.newrav1k.productservice.controller.kafka.producer.ProductCommandProducer;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;
import ru.mirea.newrav1k.productservice.service.InventoryService;
import ru.newrav1k.mirea.core.model.event.SagaCreationCancelledEvent;
import ru.newrav1k.mirea.core.model.event.SagaOrderCreationEvent;

@Slf4j
@Component
@KafkaListener(topics = {
        "${product-service.kafka.topics.order-created}",
        "${product-service.kafka.topics.order-cancelled}"
}, groupId = "${product-service.kafka.group-id}")
@RequiredArgsConstructor
public class ProductCommandConsumer {

    private final ProductCommandProducer productCommandProducer;

    private final InventoryService inventoryService;

    @KafkaHandler
    @Transactional(rollbackFor = {Exception.class})
    public void processCreationOrder(@Payload SagaOrderCreationEvent event) {
        log.info("Received SagaOrderCreatedEvent: {}", event);
        try {
            for (var product : event.products()) {
                Inventory inventory = this.inventoryService.findInventoryByProductId(product.productId());
                inventory.reserveQuantity(product.quantity());
            }
            this.productCommandProducer.processSuccessfulReserved(event.orderId(), event.customerId());
        } catch (Exception exception) {
            log.error("Error while processing SagaOrderCreatedEvent", exception);
            this.productCommandProducer.processFailureReserved(event.orderId(), event.customerId(), exception.getMessage());

            throw exception;
        }
    }

    @KafkaHandler
    @Transactional(rollbackFor = {Exception.class})
    public void processCancelledOrder(@Payload SagaCreationCancelledEvent event) {
        log.info("Received SagaProductReservationSuccessEvent: {}", event);
        try {
            for (var item : event.products()) {
                Inventory inventory = this.inventoryService.findInventoryByProductId(item.productId());
                inventory.unreserveQuantity(item.quantity());
            }
        } catch (Exception exception) {
            log.error("Error while processing SagaProductReservationSuccessEvent", exception);

            throw exception;
        }
    }

    @KafkaHandler(isDefault = true)
    public void processUnknownEvent(@Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.warn("Unknown event type from topic: {}", topic);
    }

}