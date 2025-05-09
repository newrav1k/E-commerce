package ru.newrav1k.github.orderservice.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import ru.newrav1k.github.orderservice.model.entity.Order;

import java.io.Serial;

@Getter
public class OrderDeletedEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -1226572183514663109L;

    private final Order order;

    public OrderDeletedEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

}