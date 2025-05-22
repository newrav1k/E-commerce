package ru.newrav1k.mirea.orderservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.newrav1k.mirea.core.model.payload.ProductResponse;
import ru.newrav1k.mirea.orderservice.event.OrderCancelledEvent;
import ru.newrav1k.mirea.orderservice.event.OrderChangedEvent;
import ru.newrav1k.mirea.orderservice.event.OrderCreatedEvent;
import ru.newrav1k.mirea.orderservice.exception.OrderNotFoundException;
import ru.newrav1k.mirea.orderservice.exception.ProductClientException;
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
import java.util.ArrayList;
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
        Order order = getOrderByIdOrThrow(orderId);
        return this.orderMapper.toOrderResponse(order);
    }

    public Order findOrderById(UUID orderId) {
        log.info("Finding order with id: {}", orderId);
        return getOrderByIdOrThrow(orderId);
    }

    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2),
            retryFor = {
                    FeignException.class,
            }
    )
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating new order with request: {}", request);
        Order order = new Order();

        Map<UUID, ProductResponse> products = new HashMap<>();
        List<UUID> failedIds = new ArrayList<>();
        for (var item : request.items()) {
            try {
                ProductResponse response = this.productClient.findProductById(item.productId());
                products.put(item.productId(), response);
            } catch (FeignException exception) {
                log.error("Cannot find product with id: {}", item.productId());
                failedIds.add(item.productId());
            }
        }

        if (!failedIds.isEmpty()) {
            throw new ProductClientException("Cannot find products: " + failedIds);
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

    @Retryable(
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
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

    @Retryable(
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional(rollbackFor = IOException.class)
    public OrderResponse updateOrder(UUID orderId, JsonNode patchNode) {
        log.info("Updating order with id: {}", orderId);
        Order order = getOrderByIdOrThrow(orderId);
        try {
            this.objectMapper.readerForUpdating(order).readValue(patchNode);

            this.publisher.publishEvent(new OrderChangedEvent(this, order));

            return this.orderMapper.toOrderResponse(order);
        } catch (IOException exception) {
            log.error("Error while updating order with id: {}", orderId, exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @Retryable(
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void cancelById(UUID orderId) {
        log.info("Deleting order with id: {}", orderId);
        Order order = getOrderByIdOrThrow(orderId);

        order.setStatus(OrderStatus.CANCELLED);
        this.publisher.publishEvent(new OrderCancelledEvent(this, order));

        this.orderRepository.save(order);
    }

    @Retryable(
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void updateStatus(UUID orderId, OrderStatus status) {
        log.info("Updating order status by id: {}", orderId);
        Order order = getOrderByIdOrThrow(orderId);

        if (order.getStatus() == status) {
            log.warn("Order {} already has status: {}", orderId, status);
            return;
        }

        this.orderRepository.updateStatus(orderId, status);
    }

    @Retryable(
            retryFor = {
                    OptimisticLockingFailureException.class,
                    TransientDataAccessException.class
            }
    )
    @Transactional
    public void setFailureTransaction(UUID orderId, OrderStatus status, String reason) {
        log.info("Updating order status by id: {}", orderId);
        Order order = getOrderByIdOrThrow(orderId);
        if (order.getStatus() == status && order.getReason().equals(reason)) {
            log.warn("Order {} already has status: {}", orderId, status);
            return;
        }
        this.orderRepository.updateReason(orderId, status, reason);
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
        log.info("Calculating total of orders {}", items);
        return items.stream()
                .map(item -> {
                    ProductResponse response = products.get(item.productId());
                    return response.price().multiply(BigDecimal.valueOf(item.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Recover
    public OrderResponse handleFeignException(FeignException exception, CreateOrderRequest request) {
        log.warn("recover feign exception: {}", request);
        throw new ProductClientException("product.service.price.not.available");
    }

    private Order getOrderByIdOrThrow(UUID orderId) {
        return this.orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(ORDER_NOT_FOUND));
    }

}