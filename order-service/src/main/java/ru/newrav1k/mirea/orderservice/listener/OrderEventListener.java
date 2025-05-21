package ru.newrav1k.mirea.orderservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.newrav1k.mirea.core.model.payload.ItemInformation;
import ru.newrav1k.mirea.orderservice.controller.kafka.producer.OrderCommandProducer;
import ru.newrav1k.mirea.orderservice.event.OrderCancelledEvent;
import ru.newrav1k.mirea.orderservice.event.OrderChangedEvent;
import ru.newrav1k.mirea.orderservice.event.OrderCreatedEvent;
import ru.newrav1k.mirea.orderservice.model.entity.Order;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final OrderCommandProducer orderCommandProducer;

    @TransactionalEventListener(classes = OrderCreatedEvent.class)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Order created event: {}", event);
        Order order = event.getOrder();

        this.orderCommandProducer.sendOrderCreatedProcess(
                order.getId(),
                order.getCustomerId(),
                order.getItems()
                        .stream()
                        .map(item -> new ItemInformation(
                                item.getProductId(),
                                item.getQuantity()
                        )).toList(),
                order.getTotal()
        );
    }

    @TransactionalEventListener(classes = OrderCancelledEvent.class)
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.info("Order cancelled event: {}", event);
        Order order = event.getOrder();

        this.orderCommandProducer.sendOrderCancelledProcess(
                order.getId(),
                order.getCustomerId(),
                order.getItems()
                        .stream()
                        .map(item -> new ItemInformation(
                                item.getProductId(),
                                item.getQuantity()
                        )).toList(),
                order.getTotal()
        );
    }

    @TransactionalEventListener(classes = OrderChangedEvent.class)
    public void onOrderChanged(OrderChangedEvent event) {
        log.info("Order changed event: {}", event);
    }

}