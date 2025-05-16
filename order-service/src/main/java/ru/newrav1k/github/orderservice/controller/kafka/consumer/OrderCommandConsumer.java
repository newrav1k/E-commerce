package ru.newrav1k.github.orderservice.controller.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.newrav1k.github.orderservice.model.entity.Order;
import ru.newrav1k.github.orderservice.model.enums.OrderStatus;
import ru.newrav1k.github.orderservice.service.OrderService;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationFailedEvent;
import ru.newrav1k.mirea.core.model.event.SagaProductReservationSuccessEvent;

@Slf4j
@Component
@KafkaListener(topics = {
        "${order-service.kafka.topics.product-reservation}"
}, groupId = "${order-service.kafka.group-id}")
@RequiredArgsConstructor
public class OrderCommandConsumer {

    private final OrderService orderService;

    @KafkaHandler
    @Transactional(rollbackFor = {Exception.class})
    public void processSuccessReservation(@Payload SagaProductReservationSuccessEvent event) {
        log.info("Processing success reservation: {}", event);
        try {
            Order order = this.orderService.findOrderById(event.orderId());

            if (order.getStatus() == OrderStatus.APPROVED) {
                log.info("Order already approved: {}", event.orderId());
                return;
            }

            order.setStatus(OrderStatus.APPROVED);
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
            Order order = this.orderService.findOrderById(event.orderId());

            if (order.getStatus() == OrderStatus.REJECTED) {
                log.warn("Order already rejected: {}", event.orderId());
                return;
            }

            order.setStatus(OrderStatus.REJECTED);
        } catch (Exception exception) {
            log.warn("Error processing reservation", exception);

            throw exception;
        }
    }

    @KafkaHandler(isDefault = true)
    public void processDefaultEvent() {
        log.warn("Unknown event type");
    }

}