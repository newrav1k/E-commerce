package ru.mirea.newrav1k.productservice.service;

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
import ru.mirea.newrav1k.productservice.mapper.InventoryMapper;
import ru.mirea.newrav1k.productservice.model.dto.CreateInventoryRequest;
import ru.mirea.newrav1k.productservice.model.dto.InventoryPayload;
import ru.mirea.newrav1k.productservice.model.dto.InventoryResponse;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;
import ru.mirea.newrav1k.productservice.model.entity.Product;
import ru.mirea.newrav1k.productservice.repository.InventoryRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    private final InventoryMapper inventoryMapper;

    private final ObjectMapper objectMapper;

    private final ProductService productService;

    public Page<InventoryResponse> findAll(Pageable pageable) {
        log.info("Finding all inventories");
        return this.inventoryRepository.findAll(pageable)
                .map(this.inventoryMapper::toInventoryResponse);
    }

    public InventoryResponse findById(UUID inventoryId) {
        log.info("Finding inventory with id {}", inventoryId);
        return this.inventoryRepository.findById(inventoryId)
                .map(this.inventoryMapper::toInventoryResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));
    }

    public InventoryResponse findByProductId(UUID productId) {
        log.info("Finding inventory by productId {}", productId);
        return this.inventoryRepository.findByProduct_Id(productId)
                .map(this.inventoryMapper::toInventoryResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory with product " + productId + " not found"));
    }

    public Inventory findInventoryByProductId(UUID productId) {
        log.info("Finding inventory by productId {}", productId);
        return this.inventoryRepository.findByProduct_Id(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory with product " + productId + " not found"));
    }

    @Transactional
    public InventoryResponse save(CreateInventoryRequest request) {
        log.info("Saving inventory {}", request);
        Inventory inventory = new Inventory();

        if (request.productId() != null) {
            Product product = this.productService.findProductById(request.productId());
            inventory.setProduct(product);
        }
        inventory.setQuantity(request.quantity());
        inventory.setReservedQuantity(request.reservedQuantity());

        return this.inventoryMapper.toInventoryResponse(inventory);
    }

    @Transactional
    public InventoryResponse update(UUID inventoryId, InventoryPayload inventoryPayload) {
        log.info("Updating inventory with id {}", inventoryId);
        Inventory inventory = this.inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));

        if (inventoryPayload.productId() != null) {
            Product product = this.productService.findProductById(inventoryPayload.productId());
            inventory.setProduct(product);
        }
        inventory.setQuantity(inventoryPayload.quantity());
        inventory.setReservedQuantity(inventoryPayload.reservedQuantity());

        return this.inventoryMapper.toInventoryResponse(inventory);
    }

    @Transactional
    public InventoryResponse update(UUID inventoryId, JsonNode patchNode) {
        log.info("Updating inventory with id {}", inventoryId);
        Inventory inventory = this.inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found"));

        if (patchNode.has("productId")) {
            updateProductFromPatch(inventory, patchNode);
        }

        if (patchNode.has("quantity")) {
            inventory.setQuantity(patchNode.get("quantity").asInt());
        }

        if (patchNode.has("reservedQuantity")) {
            inventory.setReservedQuantity(patchNode.get("reservedQuantity").asInt());
        }

        return this.inventoryMapper.toInventoryResponse(inventory);
    }

    @Transactional
    public void delete(UUID inventoryId) {
        log.info("Deleting inventory with id {}", inventoryId);
        this.inventoryRepository.deleteById(inventoryId);
    }

    private void updateProductFromPatch(Inventory inventory, JsonNode patchNode) {
        try {
            UUID productId = UUID.fromString(patchNode.get("productId").asText());
            Product product = this.productService.findProductById(productId);
            inventory.setProduct(product);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productId");
        }
    }

}