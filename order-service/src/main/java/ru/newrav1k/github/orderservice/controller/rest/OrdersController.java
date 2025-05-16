package ru.newrav1k.github.orderservice.controller.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.newrav1k.github.orderservice.model.dto.CreateOrderRequest;
import ru.newrav1k.github.orderservice.model.dto.OrderResponse;
import ru.newrav1k.github.orderservice.service.OrderService;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @GetMapping
    public PagedModel<OrderResponse> loadAllOrders(Pageable pageable) {
        log.info("Loading all orders");
        return new PagedModel<>(this.orderService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request,
                                                     UriComponentsBuilder uriBuilder) {
        log.info("Creating new order");
        OrderResponse order = this.orderService.createOrder(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/orders/{orderId}")
                        .build(order.id()))
                .body(order);
    }

}