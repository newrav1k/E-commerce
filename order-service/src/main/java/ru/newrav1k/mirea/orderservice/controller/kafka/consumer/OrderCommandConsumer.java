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
import ru.newrav1k.mirea.core.model.event.SagaPaymentFailureEvent;
import ru.newrav1k.mirea.core.model.event.SagaPaymentSuccessEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationFailedEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationSuccessEvent;
import ru.newrav1k.mirea.orderservice.controller.kafka.producer.OrderCommandProducer;
import ru.newrav1k.mirea.orderservice.model.dto.OrderResponse;
import ru.newrav1k.mirea.orderservice.model.enums.OrderStatus;
import ru.newrav1k.mirea.orderservice.service.OrderService;

@Slf4j
@Component
@KafkaListener(topics = {
        "${order-service.kafka.topics.product-reserved}",
        "${order-service.kafka.topics.product-failed}",
        "${order-service.kafka.topics.payment-processed}",
        "${order-service.kafka.topics.payment-failed}",
}, groupId = "${order-service.kafka.group-id}")
@RequiredArgsConstructor
public class OrderCommandConsumer {

    private final OrderCommandProducer orderCommandProducer;

    private final OrderService orderService;

    @KafkaHandler
    @Transactional(rollbackFor = {Exception.class})
    public void processSuccessReservation(@Payload SagaProductReservationSuccessEvent event) {
        log.info("Processing success reservation: {}", event);
        try {
            OrderResponse order = this.orderService.changeStatus(event.orderId(), OrderStatus.APPROVED);

            this.orderCommandProducer.sendPaymentProcess(
                    order.id(),
                    order.customerId(),
                    order.total()
            );
        } catch (Exception exception) {
            log.error("Error processing reservation", exception);

            throw exception;
        }
    }

    @KafkaHandler
    @Transactional(rollbackFor = {Exception.class})
    public void processFailedReservation(@Payload SagaProductReservationFailedEvent event) {
        log.info("Processing failed reservation: {}", event);
        try {
            this.orderService.changeStatus(event.orderId(), OrderStatus.REJECTED);
        } catch (Exception exception) {
            log.warn("Error processing reservation", exception);

            throw exception;
        }
    }

    @KafkaHandler
    public void processSuccessfulPayment(@Payload SagaPaymentSuccessEvent event) {
        log.info("Processing successful payment: {}", event);
        this.orderService.changeStatus(event.orderId(), OrderStatus.PAID);
    }

    @KafkaHandler
    public void processFailurePayment(@Payload SagaPaymentFailureEvent event) {
        log.info("Processing failure payment: {}", event);
        this.orderService.changeStatus(event.orderId(), OrderStatus.REJECTED);
    }

    @KafkaHandler(isDefault = true)
    public void processUnknownEvent(@Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.warn("Unknown event type from topic: {}", topic);
    }

}