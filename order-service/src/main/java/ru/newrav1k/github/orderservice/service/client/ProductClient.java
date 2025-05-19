package ru.newrav1k.github.orderservice.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.newrav1k.mirea.core.model.payload.ProductResponse;

import java.util.UUID;

@Service
@FeignClient(
        name = "product-service",
        url = "${order-service.services.product-service.base-url}"
)
public interface ProductClient {

    @GetMapping("/api/products/{productId}")
    ProductResponse findProductById(@PathVariable("productId") UUID productId);

}