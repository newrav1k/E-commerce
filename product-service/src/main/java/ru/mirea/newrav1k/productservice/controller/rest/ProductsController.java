package ru.mirea.newrav1k.productservice.controller.rest;

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
import ru.mirea.newrav1k.productservice.model.dto.ProductPayload;
import ru.mirea.newrav1k.productservice.service.ProductService;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductService productService;

    @GetMapping
    public PagedModel<ProductPayload> loadAllProducts(Pageable pageable) {
        log.info("Loading all products");
        return new PagedModel<>(this.productService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<ProductPayload> createProduct(@RequestBody ProductPayload payload, UriComponentsBuilder uriBuilder) {
        log.info("Creating new product");
        ProductPayload product = this.productService.create(payload);
        return ResponseEntity.created(uriBuilder
                        .replacePath("/api/products/{productId}")
                        .build(product.id()))
                .body(product);
    }

}