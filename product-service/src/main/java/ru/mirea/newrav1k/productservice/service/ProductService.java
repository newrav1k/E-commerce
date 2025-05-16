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
import ru.mirea.newrav1k.productservice.mapper.ProductMapper;
import ru.mirea.newrav1k.productservice.model.dto.CreateProductRequest;
import ru.mirea.newrav1k.productservice.model.dto.ProductPayload;
import ru.mirea.newrav1k.productservice.model.dto.ProductResponse;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;
import ru.mirea.newrav1k.productservice.model.entity.Product;
import ru.mirea.newrav1k.productservice.model.enums.ProductStatus;
import ru.mirea.newrav1k.productservice.repository.ProductRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final ObjectMapper objectMapper;

    public Page<ProductResponse> findAll(Pageable pageable) {
        log.info("Finding all products");
        return this.productRepository.findAll(pageable)
                .map(this.productMapper::toProductResponse);
    }

    public ProductResponse findById(UUID productId) {
        log.info("Finding product by id: {}", productId);
        return this.productRepository.findById(productId)
                .map(this.productMapper::toProductResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product findProductById(UUID productId) {
        log.info("Finding product by id: {}", productId);
        return this.productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        log.info("Creating product: {}", request);
        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setStatus(ProductStatus.ACTIVE);
        product.setPrice(request.price());
        product.setInventories(
                request.inventories()
                        .stream()
                        .map(initialInventoryRequest -> new Inventory(
                                product,
                                initialInventoryRequest.quantity(),
                                initialInventoryRequest.reservedQuantity()
                        )).toList()
        );
        this.productRepository.save(product);

        return this.productMapper.toProductResponse(product);
    }

    @Transactional
    public ProductResponse update(UUID productId, ProductPayload payload) {
        log.info("Updating product: {}", payload);
        return this.productRepository.findById(productId)
                .map(product -> {
                    product.setName(payload.name());
                    product.setDescription(payload.description());
                    product.setPrice(payload.price());
                    product.setStatus(payload.status());
                    return product;
                })
                .map(this.productRepository::save)
                .map(this.productMapper::toProductResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    @Transactional
    public ProductResponse update(UUID productId, JsonNode patchNode) {
        log.info("Updating product: {}", patchNode);
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
        try {
            this.objectMapper.readerForUpdating(product).readValue(patchNode);

            return this.productMapper.toProductResponse(product);
        } catch (Exception exception) {
            log.error("Error while updating product: {}", patchNode, exception);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while updating product");
        }
    }

    @Transactional
    public void delete(UUID productId) {
        log.info("Deleting product: {}", productId);
        this.productRepository.deleteById(productId);
    }

}