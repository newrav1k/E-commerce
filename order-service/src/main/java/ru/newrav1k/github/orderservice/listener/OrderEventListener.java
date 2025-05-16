package ru.newrav1k.github.orderservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.newrav1k.github.orderservice.event.OrderChangedEvent;
import ru.newrav1k.github.orderservice.event.OrderCreatedEvent;
import ru.newrav1k.github.orderservice.event.OrderDeletedEvent;
import ru.newrav1k.github.orderservice.model.entity.Order;
import ru.newrav1k.mirea.core.model.event.SagaOrderCreationEvent;
import ru.newrav1k.mirea.core.model.payload.ItemInformation;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    @Value("${order-service.kafka.topics.order-created}")
    private String orderCreatedTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(classes = OrderCreatedEvent.class)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Order created event: {}", event);
        Order order = event.getOrder();
        SagaOrderCreationEvent sagaOrderCreationEvent = new SagaOrderCreationEvent(
                order.getId(),
                order.getUserId(),
                order.getItems()
                        .stream()
                        .map(item -> new ItemInformation(item.getProductId(), item.getQuantity()))
                        .toList(),
                order.getTotal()
        );
        this.kafkaTemplate.send(this.orderCreatedTopic, sagaOrderCreationEvent);
    }

    @TransactionalEventListener(classes = OrderDeletedEvent.class)
    public void onOrderDeleted(OrderDeletedEvent event) {
        log.info("Order deleted event: {}", event);
    }

    @TransactionalEventListener(classes = OrderChangedEvent.class)
    public void onOrderChanged(OrderChangedEvent event) {
        log.info("Order changed event: {}", event);
    }

}