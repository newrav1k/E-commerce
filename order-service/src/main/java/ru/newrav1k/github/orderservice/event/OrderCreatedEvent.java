package ru.newrav1k.github.orderservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.newrav1k.github.orderservice.model.entity.Order;

import java.io.Serial;

@Getter
public class OrderCreatedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -2997415101414354468L;

    private final Order order;

    public OrderCreatedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

}