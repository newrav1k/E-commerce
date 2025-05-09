package ru.newrav1k.github.orderservice.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.newrav1k.github.orderservice.service.ItemService;
import ru.newrav1k.github.orderservice.web.dto.ItemPayload;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemsController {

    private final ItemService itemService;

    @GetMapping
    public PagedModel<ItemPayload> loadItems(Pageable pageable) {
        log.info("Loading all items");
        return new PagedModel<>(this.itemService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<ItemPayload> createItem(@RequestBody ItemPayload payload,
                                                  UriComponentsBuilder uriBuilder) {
        log.info("Saving item {}", payload);
        ItemPayload item = this.itemService.createItem(payload);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/items/{itemId}")
                        .build(item.id()))
                .body(item);
    }

}