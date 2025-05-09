package ru.newrav1k.github.orderservice.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.newrav1k.github.orderservice.service.OrderService;
import ru.newrav1k.github.orderservice.web.dto.OrderPayload;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @GetMapping
    public PagedModel<OrderPayload> loadAllOrders(Pageable pageable) {
        log.info("Loading all orders");
        return new PagedModel<>(this.orderService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<OrderPayload> createOrder(@RequestBody OrderPayload payload,
                                                    UriComponentsBuilder uriBuilder) {
        log.info("Creating new order");
        OrderPayload order = this.orderService.createOrder(payload);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/orders/{orderId}")
                        .build(order.id()))
                .body(order);
    }

}