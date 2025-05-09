package ru.newrav1k.github.orderservice.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.newrav1k.github.orderservice.service.ItemService;
import ru.newrav1k.github.orderservice.web.dto.ItemPayload;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/items/{itemId}")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<ItemPayload> loadItem(@PathVariable("itemId") UUID itemId) {
        log.info("Loading item with id: {}", itemId);
        return ResponseEntity.ok(this.itemService.findById(itemId));
    }

    @PutMapping
    public ResponseEntity<ItemPayload> updateItem(@PathVariable("itemId") UUID itemId,
                                                  @RequestBody ItemPayload itemPayload) {
        log.info("Updating item with id: {}", itemId);
        return ResponseEntity.ok(this.itemService.updateItem(itemId, itemPayload));
    }

    @PatchMapping
    public ResponseEntity<ItemPayload> patchItem(@PathVariable("itemId") UUID itemId,
                                                 @RequestBody JsonNode patchNode) {
        log.info("Updating item with id: {}", itemId);
        return ResponseEntity.ok(this.itemService.updateItem(itemId, patchNode));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") UUID itemId) {
        log.info("Deleting item with id: {}", itemId);
        this.itemService.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }

}