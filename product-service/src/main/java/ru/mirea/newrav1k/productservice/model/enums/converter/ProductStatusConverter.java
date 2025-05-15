package ru.mirea.newrav1k.productservice.model.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.mirea.newrav1k.productservice.model.enums.ProductStatus;

@Converter(autoApply = true)
public class ProductStatusConverter implements AttributeConverter<ProductStatus, String> {

    @Override
    public String convertToDatabaseColumn(ProductStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public ProductStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : ProductStatus.fromString(dbData);
    }

}