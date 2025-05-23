package ru.newrav1k.mirea.orderservice.controller.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.newrav1k.mirea.core.model.event.SagaOrderCancelledEvent;
import ru.newrav1k.mirea.core.model.event.SagaOrderConfirmedEvent;
import ru.newrav1k.mirea.core.model.event.SagaOrderCreationEvent;
import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCommandProducer {

    @Value("${order-service.kafka.topics.order-created}")
    private String orderCreatedTopic;

    @Value("${order-service.kafka.topics.order-cancelled}")
    private String orderCancelledTopic;

    @Value("${order-service.kafka.topics.order-confirmed}")
    private String orderConfirmedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreatedProcess(UUID orderId, UUID customerId, List<ItemInformation> items, BigDecimal total) {
        log.info("Sending order created process message");
        SagaOrderCreationEvent event = new SagaOrderCreationEvent(UUID.randomUUID(), orderId, customerId, items, total);
        this.kafkaTemplate.send(this.orderCreatedTopic, event);
    }

    public void sendOrderCancelledProcess(UUID orderId, UUID customerId, List<ItemInformation> items, BigDecimal total) {
        log.info("Sending order cancelled process message");
        SagaOrderCancelledEvent event = new SagaOrderCancelledEvent(UUID.randomUUID(), orderId, customerId, items, total);
        this.kafkaTemplate.send(this.orderCancelledTopic, event);
    }

    public void sendOrderConfirmedProcess(UUID orderId, UUID customerId, BigDecimal total) {
        log.info("Sending order confirmed process message");
        SagaOrderConfirmedEvent event = new SagaOrderConfirmedEvent(UUID.randomUUID(), orderId, customerId, total);
        this.kafkaTemplate.send(this.orderConfirmedTopic, event);
    }

}