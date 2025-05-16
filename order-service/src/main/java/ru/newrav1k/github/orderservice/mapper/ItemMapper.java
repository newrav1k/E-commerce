package ru.newrav1k.github.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.newrav1k.github.orderservice.model.dto.ItemResponse;
import ru.newrav1k.github.orderservice.model.entity.Item;
import ru.newrav1k.github.orderservice.model.dto.ItemPayload;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    Item toItem(ItemPayload payload);

    Item toItem(ItemResponse response);

    ItemPayload toItemPayload(Item item);

    ItemResponse toItemResponse(Item item);

}