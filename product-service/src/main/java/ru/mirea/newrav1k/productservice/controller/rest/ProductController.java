package ru.mirea.newrav1k.productservice.controller.rest;

import com.fasterxml.jackson.databind.JsonNode;
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
import ru.mirea.newrav1k.productservice.model.dto.ProductPayload;
import ru.mirea.newrav1k.productservice.service.ProductService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/products/{productId}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ProductPayload> loadProduct(@PathVariable("productId") UUID productId) {
        log.info("Loading product {}", productId);
        ProductPayload product = this.productService.findById(productId);
        return ResponseEntity.ok(product);
    }

    @PutMapping
    public ResponseEntity<ProductPayload> updateProduct(@PathVariable("productId") UUID productId,
                                                        @RequestBody ProductPayload payload) {
        log.info("Updating product {}", productId);
        ProductPayload result = this.productService.update(productId, payload);
        return ResponseEntity.ok(result);
    }

    @PatchMapping
    public ResponseEntity<ProductPayload> patchProduct(@PathVariable("productId") UUID productId,
                                                       @RequestBody JsonNode patchNode) {
        log.info("Updating product {}", productId);
        ProductPayload result = this.productService.update(productId, patchNode);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@PathVariable("productId") UUID productId) {
        log.info("Deleting product {}", productId);
        this.productService.delete(productId);
        return ResponseEntity.noContent().build();
    }

}