package ru.newrav1k.mirea.orderservice.service;

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
import ru.newrav1k.mirea.core.model.payload.ProductResponse;
import ru.newrav1k.mirea.orderservice.event.OrderChangedEvent;
import ru.newrav1k.mirea.orderservice.event.OrderCreatedEvent;
import ru.newrav1k.mirea.orderservice.event.OrderDeletedEvent;
import ru.newrav1k.mirea.orderservice.exception.OrderNotFoundException;
import ru.newrav1k.mirea.orderservice.mapper.OrderMapper;
import ru.newrav1k.mirea.orderservice.model.dto.CreateOrderRequest;
import ru.newrav1k.mirea.orderservice.model.dto.OrderPayload;
import ru.newrav1k.mirea.orderservice.model.dto.OrderResponse;
import ru.newrav1k.mirea.orderservice.model.entity.Item;
import ru.newrav1k.mirea.orderservice.model.entity.Order;
import ru.newrav1k.mirea.orderservice.model.enums.OrderStatus;
import ru.newrav1k.mirea.orderservice.repository.OrderRepository;
import ru.newrav1k.mirea.orderservice.service.client.ProductClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.newrav1k.mirea.orderservice.utils.MessageCode.ORDER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ObjectMapper objectMapper;

    private final ApplicationEventPublisher publisher;

    private final ProductClient productClient;

    public Page<OrderResponse> findAll(Pageable pageable) {
        log.info("Finding all orders");
        return orderRepository.findAll(pageable)
                .map(this.orderMapper::toOrderResponse);
    }

    public OrderResponse findById(UUID orderId) {
        log.info("Finding order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        return this.orderMapper.toOrderResponse(order);
    }

    public Order findOrderById(UUID orderId) {
        log.info("Finding order with id: {}", orderId);
        return this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating new order with request: {}", request);
        Order order = new Order();

        Map<UUID, ProductResponse> products = new HashMap<>();
        for (var item : request.items()) {
            ProductResponse response = this.productClient.findProductById(item.productId());
            products.put(item.productId(), response);
        }

        order.setCustomerId(request.customerId());
        order.setStatus(OrderStatus.PENDING);
        order.setItems(buildItems(order, products, request.items()));
        order.setTotal(calculateTotalPrice(products, request.items()));

        this.orderRepository.save(order);

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(this, order);
        this.publisher.publishEvent(orderCreatedEvent);

        return this.orderMapper.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse updateOrder(UUID orderId, OrderPayload payload) {
        log.info("Updating order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .map(it -> {
                    it.setCustomerId(payload.customerId());
                    it.setStatus(payload.status());
                    it.setTotal(payload.total());
                    return it;
                })
                .map(this.orderRepository::save)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        this.publisher.publishEvent(new OrderChangedEvent(this, order));
        return this.orderMapper.toOrderResponse(order);
    }

    @Transactional(rollbackFor = IOException.class)
    public OrderResponse updateOrder(UUID orderId, JsonNode patchNode) {
        log.info("Updating order with id: {}", orderId);
        Order order = this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
        try {
            this.objectMapper.readerForUpdating(order).readValue(patchNode);

            this.publisher.publishEvent(new OrderChangedEvent(this, order));

            return this.orderMapper.toOrderResponse(order);
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

    private List<Item> buildItems(Order order,
                                  Map<UUID, ProductResponse> products,
                                  List<CreateOrderRequest.ItemRequest> items) {
        log.info("Building list of order items");
        return items.stream()
                .map(item -> new Item(order,
                        item.productId(),
                        item.quantity(),
                        products.get(item.productId()).price())
                ).toList();
    }

    public BigDecimal calculateTotalPrice(Map<UUID, ProductResponse> products,
                                          List<CreateOrderRequest.ItemRequest> items) {
        log.info("Calculating total of orders");
        BigDecimal total = BigDecimal.ZERO;
        for (var item : items) {
            ProductResponse response = products.get(item.productId());
            total = total.add(response.price().multiply(BigDecimal.valueOf(item.quantity())));
        }
        return total;
    }

}