package ru.newrav1k.github.orderservice.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.newrav1k.github.orderservice.model.dto.OrderPayload;
import ru.newrav1k.github.orderservice.model.dto.OrderResponse;
import ru.newrav1k.github.orderservice.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders/{orderId}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<OrderResponse> loadOrder(@PathVariable("orderId") UUID orderId) {
        log.info("Loading order with id: {}", orderId);
        OrderResponse payload = this.orderService.findById(orderId);
        return ResponseEntity.ok(payload);
    }

    @PutMapping
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderPayload payload) {
        log.info("Updating order with id: {}", orderId);
        OrderResponse resultPayload = this.orderService.updateOrder(orderId, payload);
        return ResponseEntity.ok(resultPayload);
    }

    @PatchMapping
    public ResponseEntity<OrderResponse> patchOrder(@PathVariable("orderId") UUID orderId, @RequestBody JsonNode patchNode) {
        log.info("Updating order with id: {}", orderId);
        OrderResponse payload = this.orderService.updateOrder(orderId, patchNode);
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") UUID orderId) {
        log.info("Deleting order with id: {}", orderId);
        this.orderService.deleteById(orderId);
        return ResponseEntity.noContent().build();
    }

}