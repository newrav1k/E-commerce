package ru.newrav1k.mirea.orderservice.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import ru.newrav1k.mirea.orderservice.model.dto.ItemResponse;
import ru.newrav1k.mirea.orderservice.model.dto.UpdateItemRequest;
import ru.newrav1k.mirea.orderservice.service.ItemService;

import java.util.UUID;

@Tag(
        name = "Item Controller",
        description = "Контроллер для управления определёнными позициями в заказе"
)
@Slf4j
@RestController
@RequestMapping("/api/items/{itemId}")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @Operation(
            summary = "Загрузка позиции заказа",
            description = "Загружает позицию заказа по её идентификатору"
    )
    @GetMapping
    public ResponseEntity<ItemResponse> loadItem(@PathVariable("itemId") UUID itemId) {
        log.info("Loading item with id: {}", itemId);
        return ResponseEntity.ok(this.itemService.findById(itemId));
    }

    @Operation(
            summary = "Обновление позиции заказа",
            description = "Полностью обновляет позицию заказа по её идентификатору"
    )
    @PutMapping
    public ResponseEntity<ItemResponse> updateItem(@PathVariable("itemId") UUID itemId,
                                                   @Valid @RequestBody UpdateItemRequest request) {
        log.info("Updating item with id: {}", itemId);
        return ResponseEntity.ok(this.itemService.updateItem(itemId, request));
    }

    @Operation(
            summary = "Обновление позиции заказа",
            description = "Частично обновляет позицию заказа по её идентификатору"
    )
    @PatchMapping
    public ResponseEntity<ItemResponse> patchItem(@PathVariable("itemId") UUID itemId,
                                                  @RequestBody JsonNode patchNode) {
        log.info("Updating item with id: {}", itemId);
        return ResponseEntity.ok(this.itemService.updateItem(itemId, patchNode));
    }

    @Operation(
            summary = "Удаляет позицию из заказа",
            description = "Удаляет позицию заказа по её идентификатору"
    )
    @DeleteMapping
    public ResponseEntity<Void> deleteItem(@PathVariable("itemId") UUID itemId) {
        log.info("Deleting item with id: {}", itemId);
        this.itemService.deleteById(itemId);
        return ResponseEntity.noContent().build();
    }

}