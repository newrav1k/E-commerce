package ru.newrav1k.mirea.orderservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.newrav1k.mirea.core.model.event.SagaPaymentSuccessEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationFailedEvent;
import ru.newrav1k.mirea.orderservice.exception.OrderNotFoundException;
import ru.newrav1k.mirea.orderservice.model.enums.OrderStatus;
import ru.newrav1k.mirea.orderservice.service.OrderService;

@Slf4j
@Component
@KafkaListener(topics = {
        "${order-service.kafka.topics.product-failed}",
        "${order-service.kafka.topics.payment-processed}",
        "${order-service.kafka.topics.payment-failed}",
}, groupId = "${order-service.kafka.group-id}")
@RequiredArgsConstructor
public class OrderCommandConsumer {

    private final OrderService orderService;

    @KafkaHandler
    @Transactional
    public void processFailedReservation(@Payload SagaProductReservationFailedEvent event) {
        log.info("Processing failed reservation: {}", event);
        try {
            this.orderService.updateStatus(event.orderId(), OrderStatus.REJECTED);
        } catch (OrderNotFoundException exception) {
            log.error("Order not found: {}", exception.getMessage(), exception);
            throw exception;
        } catch (Exception exception) {
            log.error("Error processing reservation", exception);
            throw exception;
        }
    }

    @KafkaHandler
    @Transactional
    public void processSuccessPayment(@Payload SagaPaymentSuccessEvent event) {
        log.info("Processing successful payment: {}", event);
        try {
            this.orderService.updateStatus(event.orderId(), OrderStatus.PAID);

            // TODO: уведомление о создании заказа

        } catch (OrderNotFoundException exception) {
            log.error("Order not found: {}", exception.getMessage(), exception);
            throw exception;
        } catch (Exception exception) {
            log.error("Error processing success payment", exception);
            throw exception;
        }
    }

    @KafkaHandler(isDefault = true)
    public void processUnknownEvent(@Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.warn("Unknown event type from topic: {}", topic);
    }

}