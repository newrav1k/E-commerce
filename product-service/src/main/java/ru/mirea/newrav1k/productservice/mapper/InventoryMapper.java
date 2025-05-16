package ru.mirea.newrav1k.productservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.mirea.newrav1k.productservice.model.dto.InventoryPayload;
import ru.mirea.newrav1k.productservice.model.dto.InventoryResponse;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {

    @Mapping(target = "product", ignore = true)
    Inventory toInventory(InventoryPayload payload);

    Inventory toInventory(InventoryResponse response);

    @Mapping(target = "productId", source = "product.id")
    InventoryPayload toInventoryPayload(Inventory inventory);

    InventoryResponse toInventoryResponse(Inventory inventory);

}