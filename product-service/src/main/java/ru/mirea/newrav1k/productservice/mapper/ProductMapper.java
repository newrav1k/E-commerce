package ru.mirea.newrav1k.productservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.mirea.newrav1k.productservice.model.dto.ProductPayload;
import ru.mirea.newrav1k.productservice.model.dto.ProductResponse;
import ru.mirea.newrav1k.productservice.model.entity.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    Product toProduct(ProductPayload payload);

    Product toProduct(ProductResponse response);

    ProductPayload toProductPayload(Product product);

    ProductResponse toProductResponse(Product product);

}