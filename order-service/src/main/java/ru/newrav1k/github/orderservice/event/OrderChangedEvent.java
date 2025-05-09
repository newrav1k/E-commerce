package ru.newrav1k.github.orderservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.newrav1k.github.orderservice.model.entity.Order;

import java.io.Serial;

@Getter
public class OrderChangedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -3285455477490525534L;

    private final Order order;

    public OrderChangedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

}