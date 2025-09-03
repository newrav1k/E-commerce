package ru.newrav1k.mirea.orderservice.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import ru.newrav1k.mirea.orderservice.model.dto.OrderPayload;
import ru.newrav1k.mirea.orderservice.model.dto.OrderResponse;
import ru.newrav1k.mirea.orderservice.service.OrderService;

import java.util.UUID;

@Tag(
        name = "Order Controller",
        description = "Контроллер для управления определённым заказом"
)
@Slf4j
@RestController
@RequestMapping("/api/orders/{orderId}")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Загрузка заказа",
            description = "Загружает заказ по его идентификатору"
    )
    @ApiResponse(responseCode = "401", description = "Такого заказа не существует")
    @GetMapping
    public ResponseEntity<OrderResponse> loadOrder(@PathVariable("orderId") UUID orderId) {
        log.info("Loading order with id: {}", orderId);
        OrderResponse payload = this.orderService.findById(orderId);
        return ResponseEntity.ok(payload);
    }

    @Operation(
            summary = "Обновление заказа",
            description = "Полностью обновляет заказ по его идентификатору"
    )
    @ApiResponse(responseCode = "401", description = "Такого заказа не существует")
    @PutMapping
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable("orderId") UUID orderId, @RequestBody OrderPayload payload) {
        log.info("Updating order with id: {}", orderId);
        OrderResponse resultPayload = this.orderService.updateOrder(orderId, payload);
        return ResponseEntity.ok(resultPayload);
    }

    @Operation(
            summary = "Обновление заказа",
            description = "Частично обновляет заказ по его идентификатору"
    )
    @ApiResponse(responseCode = "401", description = "Такого заказа не существует")
    @PatchMapping
    public ResponseEntity<OrderResponse> patchOrder(@PathVariable("orderId") UUID orderId, @RequestBody JsonNode patchNode) {
        log.info("Updating order with id: {}", orderId);
        OrderResponse payload = this.orderService.updateOrder(orderId, patchNode);
        return ResponseEntity.ok(payload);
    }

    @Operation(
            summary = "Отмена заказа",
            description = "Отменяет заказ по его идентификатору"
    )
    @DeleteMapping
    public ResponseEntity<Void> cancelOrder(@PathVariable("orderId") UUID orderId) {
        log.info("Deleting order with id: {}", orderId);
        this.orderService.cancelById(orderId);
        return ResponseEntity.noContent().build();
    }

}