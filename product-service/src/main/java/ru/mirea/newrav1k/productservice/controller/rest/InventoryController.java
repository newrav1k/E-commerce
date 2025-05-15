package ru.mirea.newrav1k.productservice.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.mirea.newrav1k.productservice.model.dto.InventoryPayload;
import ru.mirea.newrav1k.productservice.service.InventoryService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public PagedModel<InventoryPayload> loadAllInventory(Pageable pageable) {
        log.info("Loading all inventory");
        return new PagedModel<>(this.inventoryService.findAll(pageable));
    }

    @GetMapping("/{inventoryId}")
    public ResponseEntity<InventoryPayload> loadInventory(@PathVariable("inventoryId") UUID inventoryId) {
        log.info("Loading a inventory with id {}", inventoryId);
        InventoryPayload inventory = this.inventoryService.findById(inventoryId);
        return ResponseEntity.ok(inventory);
    }

    @PostMapping
    public ResponseEntity<InventoryPayload> createInventory(@RequestBody InventoryPayload inventoryPayload,
                                                            UriComponentsBuilder uriBuilder) {
        log.info("Creating a inventory");
        InventoryPayload inventory = this.inventoryService.save(inventoryPayload);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/inventory/{inventoryId}")
                        .build(inventory.id()))
                .body(inventory);
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<InventoryPayload> updateInventory(@PathVariable("inventoryId") UUID inventoryId,
                                                            @RequestBody InventoryPayload inventoryPayload) {
        log.info("Updating inventory with id {}", inventoryId);
        InventoryPayload inventory = this.inventoryService.update(inventoryId, inventoryPayload);
        return ResponseEntity.ok(inventory);
    }

    @PatchMapping("/{inventoryId}")
    public ResponseEntity<InventoryPayload> patchInventory(@PathVariable("inventoryId") UUID inventoryId,
                                                           @RequestBody JsonNode patchNode) {
        log.info("Updating inventory with id {}", inventoryId);
        InventoryPayload inventory = this.inventoryService.update(inventoryId, patchNode);
        return ResponseEntity.ok(inventory);
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable("inventoryId") UUID inventoryId) {
        log.info("Deleting inventory with id {}", inventoryId);
        this.inventoryService.delete(inventoryId);
        return ResponseEntity.noContent().build();
    }

}