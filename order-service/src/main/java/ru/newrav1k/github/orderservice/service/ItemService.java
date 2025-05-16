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
import ru.newrav1k.github.orderservice.mapper.ItemMapper;
import ru.newrav1k.github.orderservice.mapper.OrderMapper;
import ru.newrav1k.github.orderservice.model.dto.CreateItemRequest;
import ru.newrav1k.github.orderservice.model.dto.ItemResponse;
import ru.newrav1k.github.orderservice.model.entity.Item;
import ru.newrav1k.github.orderservice.model.entity.Order;
import ru.newrav1k.github.orderservice.repository.ItemRepository;
import ru.newrav1k.github.orderservice.model.dto.ItemPayload;

import java.io.IOException;
import java.util.UUID;

import static ru.newrav1k.github.orderservice.utils.MessageCode.ITEM_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;

    private final OrderMapper orderMapper;

    private final ObjectMapper objectMapper;

    private final OrderService orderService;

    public Page<ItemResponse> findAll(Pageable pageable) {
        log.info("Finding all items");
        return this.itemRepository.findAll(pageable)
                .map(this.itemMapper::toItemResponse);
    }

    public ItemResponse findById(UUID itemId) {
        log.info("Finding a item with id: {}", itemId);
        return this.itemRepository.findById(itemId)
                .map(this.itemMapper::toItemResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
    }

    @Transactional
    public ItemResponse createItem(CreateItemRequest request) {
        log.info("Creating new item");
        Item item = new Item();

        Order order = this.orderService.findOrderById(request.orderId());

        item.setOrder(order);
        item.setProductId(request.productId());
        item.setQuantity(request.quantity());
        item.setPrice(request.price());

        this.itemRepository.save(item);

        return this.itemMapper.toItemResponse(item);
    }

    @Transactional
    public ItemResponse updateItem(UUID itemId, ItemPayload itemPayload) {
        log.info("Updating a item with id: {}", itemId);
        return this.itemRepository.findById(itemId)
                .map(item -> {
                    item.setPrice(itemPayload.price());
                    item.setQuantity(itemPayload.quantity());
                    return item;
                })
                .map(this.itemRepository::save)
                .map(this.itemMapper::toItemResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
    }

    @Transactional(rollbackFor = IOException.class)
    public ItemResponse updateItem(UUID itemId, JsonNode patchNode) {
        log.info("Updating a item with id: {}", itemId);
        Item item = this.itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
        try {
            this.objectMapper.readerForUpdating(item).readValue(patchNode);

            return this.itemMapper.toItemResponse(item);
        } catch (IOException exception) {
            log.error("Error while reading patch {}", patchNode, exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @Transactional
    public void deleteById(UUID itemId) {
        log.info("Deleting a item with id: {}", itemId);
        this.itemRepository.deleteById(itemId);
    }

}