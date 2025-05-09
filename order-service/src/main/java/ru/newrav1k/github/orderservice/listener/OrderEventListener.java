package ru.newrav1k.github.orderservice.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;
import ru.newrav1k.github.orderservice.event.OrderChangedEvent;
import ru.newrav1k.github.orderservice.event.OrderDeletedEvent;

@Slf4j
@Service
public class OrderEventListener {

    @TransactionalEventListener(classes = OrderDeletedEvent.class)
    public void onOrderDeleted(OrderDeletedEvent event) {
        log.info("Order deleted event: {}", event);
    }

    @TransactionalEventListener(classes = OrderChangedEvent.class)
    public void onOrderChanged(OrderChangedEvent event) {
        log.info("Order changed event: {}", event);
    }

}