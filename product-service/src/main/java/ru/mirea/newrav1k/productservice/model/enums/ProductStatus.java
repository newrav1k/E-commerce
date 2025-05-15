package ru.mirea.newrav1k.productservice.model.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {
    ACTIVE, INACTIVE;

    public static ProductStatus fromString(String string) {
        for (ProductStatus productStatus : ProductStatus.values()) {
            if (productStatus.name().equalsIgnoreCase(string)) {
                return productStatus;
            }
        }
        throw new IllegalArgumentException("Invalid product status: " + string);
    }

}