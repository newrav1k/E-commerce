package ru.newrav1k.github.orderservice.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.newrav1k.github.orderservice.model.dto.CreateItemRequest;
import ru.newrav1k.github.orderservice.model.dto.ItemResponse;
import ru.newrav1k.github.orderservice.service.ItemService;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemsController {

    private final ItemService itemService;

    @GetMapping
    public PagedModel<ItemResponse> loadItems(Pageable pageable) {
        log.info("Loading all items");
        return new PagedModel<>(this.itemService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@Valid @RequestBody CreateItemRequest request,
                                                   UriComponentsBuilder uriBuilder) {
        log.info("Saving item {}", request);
        ItemResponse item = this.itemService.createItem(request);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/items/{itemId}")
                        .build(item.id()))
                .body(item);
    }

}