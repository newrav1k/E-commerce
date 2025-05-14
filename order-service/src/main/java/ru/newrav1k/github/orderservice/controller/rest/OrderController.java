package ru.newrav1k.github.orderservice.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.newrav1k.github.orderservice.service.OrderService;
import ru.newrav1k.github.orderservice.model.dto.OrderPayload;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders/{orderId}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<OrderPayload> loadOrder(@PathVariable("orderId") UUID orderId) {
        log.info("Loading order with id: {}", orderId);
        OrderPayload payload = this.orderService.findById(orderId);
        return ResponseEntity.ok(payload);
    }

    @PutMapping
    public ResponseEntity<OrderPayload> updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderPayload payload) {
        log.info("Updating order with id: {}", orderId);
        OrderPayload resultPayload = this.orderService.updateOrder(orderId, payload);
        return ResponseEntity.ok(resultPayload);
    }

    @PatchMapping
    public ResponseEntity<OrderPayload> patchOrder(@PathVariable("orderId") UUID orderId, @RequestBody JsonNode patchNode) {
        log.info("Updating order with id: {}", orderId);
        OrderPayload payload = this.orderService.updateOrder(orderId, patchNode);
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") UUID orderId) {
        log.info("Deleting order with id: {}", orderId);
        this.orderService.deleteById(orderId);
        return ResponseEntity.noContent().build();
    }

}