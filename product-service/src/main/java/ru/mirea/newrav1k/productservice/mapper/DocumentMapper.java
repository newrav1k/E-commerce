package ru.mirea.newrav1k.productservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;
import ru.mirea.newrav1k.productservice.model.entity.Product;
import ru.mirea.newrav1k.productservice.model.entity.document.InventoryDocument;
import ru.mirea.newrav1k.productservice.model.entity.document.ProductDocument;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {

    ProductDocument toProductDocument(Product product);

    Product toProduct(ProductDocument productDocument);

    InventoryDocument toInventoryDocument(Inventory inventory);

    Inventory toInventory(InventoryDocument inventoryDocument);

}