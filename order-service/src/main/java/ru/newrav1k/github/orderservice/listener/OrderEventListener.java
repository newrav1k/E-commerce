package ru.newrav1k.github.orderservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.newrav1k.github.orderservice.event.OrderChangedEvent;
import ru.newrav1k.github.orderservice.event.OrderCreatedEvent;
import ru.newrav1k.github.orderservice.event.OrderDeletedEvent;
import ru.newrav1k.github.orderservice.model.entity.Item;
import ru.newrav1k.github.orderservice.model.entity.Order;
import ru.newrav1k.mirea.core.model.event.SagaOrderCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("onOrderCreated: {}", event);
        Order order = event.getOrder();
        SagaOrderCreatedEvent sagaOrderCreatedEvent = new SagaOrderCreatedEvent(
                order.getId(),
                order.getUserId(),
                order.getItems().stream()
                        .map(Item::getProductId)
                        .toList()
        );
        this.kafkaTemplate.send("order-events", sagaOrderCreatedEvent);
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