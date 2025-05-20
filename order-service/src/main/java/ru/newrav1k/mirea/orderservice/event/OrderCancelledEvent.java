package ru.newrav1k.mirea.orderservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.newrav1k.mirea.orderservice.model.entity.Order;

import java.io.Serial;

@Getter
public class OrderCancelledEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -1226572183514663109L;

    private final Order order;

    public OrderCancelledEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

}