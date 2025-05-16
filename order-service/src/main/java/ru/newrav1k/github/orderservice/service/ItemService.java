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
import ru.newrav1k.github.orderservice.model.entity.Item;
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

    public Page<ItemPayload> findAll(Pageable pageable) {
        log.info("Finding all items");
        return this.itemRepository.findAll(pageable)
                .map(this.itemMapper::toItemPayload);
    }

    public ItemPayload findById(UUID itemId) {
        log.info("Finding a item with id: {}", itemId);
        return this.itemRepository.findById(itemId)
                .map(this.itemMapper::toItemPayload)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
    }

    @Transactional
    public ItemPayload createItem(ItemPayload itemPayload) {
        log.info("Creating new item");
        Item item = this.itemMapper.toItem(itemPayload);
        Item result = this.itemRepository.save(item);
        return this.itemMapper.toItemPayload(result);
    }

    @Transactional
    public ItemPayload updateItem(UUID itemId, ItemPayload itemPayload) {
        log.info("Updating a item with id: {}", itemId);
        return this.itemRepository.findById(itemId)
                .map(item -> {
                    item.setPrice(itemPayload.price());
                    item.setQuantity(itemPayload.quantity());
                    return item;
                })
                .map(this.itemRepository::save)
                .map(this.itemMapper::toItemPayload)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
    }

    @Transactional(rollbackFor = IOException.class)
    public ItemPayload updateItem(UUID itemId, JsonNode patchNode) {
        log.info("Updating a item with id: {}", itemId);
        Item item = this.itemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ITEM_NOT_FOUND));
        try {
            this.objectMapper.readerForUpdating(item).readValue(patchNode);

            return this.itemMapper.toItemPayload(item);
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
