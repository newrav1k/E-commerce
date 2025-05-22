package ru.mirea.newrav1k.productservice.controller.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationFailedEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationSuccessEvent;
import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCommandProducer {

    @Value("${product-service.kafka.topics.product-reserved}")
    private String productReservedTopic;

    @Value("${product-service.kafka.topics.product-failed}")
    private String productFailedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void processSuccessReserved(UUID orderId, UUID customerId, List<ItemInformation> products, BigDecimal total) {
        log.info("Sending product reserved event to topic {}", this.productReservedTopic);
        SagaProductReservationSuccessEvent event = new SagaProductReservationSuccessEvent(UUID.randomUUID(), orderId, customerId, products, total);
        this.kafkaTemplate.send(this.productReservedTopic, event);
    }

    public void processFailureReserved(UUID orderId, UUID customerId, String reason) {
        log.info("Sending product failed event to topic {}", this.productFailedTopic);
        SagaProductReservationFailedEvent event = new SagaProductReservationFailedEvent(UUID.randomUUID(), orderId, customerId, reason);
        this.kafkaTemplate.send(this.productFailedTopic, event);
    }

}