package ru.mirea.newrav1k.productservice.controller.kafka.consumer;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.mirea.newrav1k.productservice.controller.kafka.producer.ProductCommandProducer;
import ru.mirea.newrav1k.productservice.service.InventoryService;
import ru.newrav1k.mirea.core.model.event.SagaOrderCancelledEvent;
import ru.newrav1k.mirea.core.model.event.SagaOrderCreationEvent;
import ru.newrav1k.mirea.core.model.event.SagaPaymentFailureEvent;
import ru.newrav1k.mirea.core.model.payload.ItemInformation;

import java.util.List;

@Slf4j
@Component
@KafkaListener(topics = {
        "${product-service.kafka.topics.order-created}",
        "${product-service.kafka.topics.order-cancelled}",
        "${product-service.kafka.topics.payment-failed}"
}, groupId = "${product-service.kafka.group-id}")
@RequiredArgsConstructor
public class ProductCommandConsumer {

    private final ProductCommandProducer productCommandProducer;

    private final InventoryService inventoryService;

    @KafkaHandler
    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {
                    OptimisticLockException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void processCreationOrder(@Payload SagaOrderCreationEvent event) {
        log.info("Received order created event: {}", event);
        try {
            reserveProducts(event.products());

            this.productCommandProducer.processSuccessReserved(event.orderId(), event.customerId(), event.products(), event.total());
        } catch (Exception exception) {
            log.error("Error while processing SagaOrderCreatedEvent", exception);
            this.productCommandProducer.processFailureReserved(event.orderId(), event.customerId(), exception.getMessage());
            throw exception;
        }
    }

    @KafkaHandler
    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void processCancelledOrder(@Payload SagaOrderCancelledEvent event) {
        log.info("Received order cancelled event: {}", event);
        try {
            unreserveProducts(event.products());
        } catch (IllegalArgumentException exception) {
            log.error("Error while processing unreserved products", exception);
            throw exception;
        } catch (Exception exception) {
            log.error("Error while processing SagaProductReservationSuccessEvent", exception);
            throw exception;
        }
    }

    @KafkaHandler
    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void processFailurePayment(@Payload SagaPaymentFailureEvent event) {
        log.info("Received payment failure event: {}", event);
        try {
            unreserveProducts(event.products());

            this.productCommandProducer.processFailureReserved(event.orderId(), event.customerId(), event.reason());
        } catch (IllegalArgumentException exception) {
            log.error("Error while reservation product: {}", event.products(), exception);
            this.productCommandProducer.processFailureReserved(event.orderId(), event.customerId(), "Reservation failed");
            throw exception;
        } catch (Exception exception) {
            log.error("Error while processing SagaPaymentFailureEvent", exception);
            throw exception;
        }
    }

    @KafkaHandler(isDefault = true)
    public void processUnknownEvent(@Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic,
                                    @Payload Object payload) {
        log.warn("Unknown event type from topic {} with payload {}", topic, payload);
    }

    private void reserveProducts(List<ItemInformation> items) {
        items.forEach(item -> this.inventoryService
                .updateReservation(item.productId(), item.quantity(), true));
    }

    private void unreserveProducts(List<ItemInformation> items) {
        items.forEach(item -> this.inventoryService
                .updateReservation(item.productId(), item.quantity(), false));
    }

}