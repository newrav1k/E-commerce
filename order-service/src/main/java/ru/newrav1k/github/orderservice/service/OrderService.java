package ru.newrav1k.github.orderservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.newrav1k.github.orderservice.event.OrderChangedEvent;
import ru.newrav1k.github.orderservice.event.OrderCreatedEvent;
import ru.newrav1k.github.orderservice.event.OrderDeletedEvent;
import ru.newrav1k.github.orderservice.exception.OrderNotFoundException;
import ru.newrav1k.github.orderservice.mapper.OrderMapper;
import ru.newrav1k.github.orderservice.model.dto.OrderPayload;
import ru.newrav1k.github.orderservice.model.entity.Order;
import ru.newrav1k.github.orderservice.repository.OrderRepository;

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

    private final ApplicationEventPublisher publisher;

    public Page<OrderPayload> findAll(Pageable pageable) {
        log.info("Finding all orders");
        return orderRepository.findAll(pageable)
                .map(this.orderMapper::toOrderPayload);
    }

    public OrderPayload findById(UUID orderId) {
        log.info("Finding order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        return this.orderMapper.toOrderPayload(order);
    }

    public Order findOrderById(UUID orderId) {
        log.info("Finding order with id: {}", orderId);
        return this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
    }

    @Transactional
    public OrderPayload createOrder(OrderPayload payload) {
        log.info("Creating new order");
        Order order = this.orderRepository.save(this.orderMapper.toOrder(payload));

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(this, order);
        this.publisher.publishEvent(orderCreatedEvent);

        return this.orderMapper.toOrderPayload(order);
    }

    @Transactional
    public OrderPayload updateOrder(UUID orderId, OrderPayload payload) {
        log.info("Updating order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .map(it -> {
                    it.setUserId(payload.userId());
                    it.setStatus(payload.status());
                    it.setTotal(payload.total());
                    return it;
                })
                .map(this.orderRepository::save)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        this.publisher.publishEvent(new OrderChangedEvent(this, order));
        return this.orderMapper.toOrderPayload(order);
    }

    @Transactional(rollbackFor = IOException.class)
    public OrderPayload updateOrder(UUID orderId, JsonNode patchNode) {
        log.info("Updating order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        try {
            this.objectMapper.readerForUpdating(order).readValue(patchNode);

            this.publisher.publishEvent(new OrderChangedEvent(this, order));

            return this.orderMapper.toOrderPayload(order);
        } catch (IOException exception) {
            log.error("Error while updating order with id: {}", orderId, exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @Transactional
    public void deleteById(UUID orderId) {
        log.info("Deleting order with id: {}", orderId);
        this.orderRepository.findById(orderId)
                .ifPresent(order -> {
                    this.publisher.publishEvent(new OrderDeletedEvent(this, order));
                    this.orderRepository.delete(order);
                });
    }

}