package ru.newrav1k.github.orderservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.newrav1k.github.orderservice.mapper.OrderMapper;
import ru.newrav1k.github.orderservice.model.entity.Order;
import ru.newrav1k.github.orderservice.repository.OrderRepository;
import ru.newrav1k.github.orderservice.web.dto.OrderPayload;

import java.io.IOException;
import java.util.UUID;

import static ru.newrav1k.github.orderservice.utils.MessageCode.ORDER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ObjectMapper objectMapper;

    public Page<OrderPayload> findAll(Pageable pageable) {
        log.info("Finding all orders");
        return orderRepository.findAll(pageable)
                .map(this.orderMapper::toOrderPayload);
    }

    public OrderPayload findById(UUID orderId) {
        log.info("Finding order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND));
        return this.orderMapper.toOrderPayload(order);
    }

    @Transactional
    public OrderPayload createOrder(OrderPayload payload) {
        log.info("Creating new order");
        Order order = this.orderMapper.toOrder(payload);
        Order result = this.orderRepository.save(order);
        return this.orderMapper.toOrderPayload(result);
    }

    @Transactional
    public OrderPayload updateOrder(UUID orderId, OrderPayload payload) {
        log.info("Updating order with id: {}", orderId);
        return this.orderRepository.findById(orderId)
                .map(order -> {
                    order.setUserId(payload.userId());
                    order.setStatus(payload.status());
                    order.setTotal(payload.total());
                    return order;
                })
                .map(this.orderMapper::toOrderPayload)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND));
    }

    @Transactional
    public OrderPayload updateOrder(UUID orderId, JsonNode patchNode) {
        log.info("Updating order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ORDER_NOT_FOUND));
        try {
            this.objectMapper.readerForUpdating(order).readValue(patchNode);

            Order resultOrder = this.orderRepository.save(order);
            return this.orderMapper.toOrderPayload(resultOrder);
        } catch (IOException exception) {
            log.warn("Error while updating order with id: {}", orderId, exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    @Transactional
    public void deleteById(UUID orderId) {
        log.info("Deleting order with id: {}", orderId);
        this.orderRepository.deleteById(orderId);
    }

}