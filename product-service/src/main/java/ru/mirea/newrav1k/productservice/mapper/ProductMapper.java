package ru.mirea.newrav1k.productservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.mirea.newrav1k.productservice.model.dto.ProductPayload;
import ru.mirea.newrav1k.productservice.model.entity.Product;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {

    Product toProduct(ProductPayload payload);

    ProductPayload toProductPayload(Product product);

}