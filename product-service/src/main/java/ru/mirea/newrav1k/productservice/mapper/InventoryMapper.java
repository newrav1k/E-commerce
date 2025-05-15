package ru.mirea.newrav1k.productservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.mirea.newrav1k.productservice.model.dto.InventoryPayload;
import ru.mirea.newrav1k.productservice.model.entity.Inventory;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {

    Inventory toInventory(InventoryPayload payload);

    InventoryPayload toInventoryPayload(Inventory inventory);

}