package ru.newrav1k.github.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.newrav1k.github.orderservice.model.entity.Item;
import ru.newrav1k.github.orderservice.model.dto.ItemPayload;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    Item toItem(ItemPayload payload);

    ItemPayload toItemPayload(Item item);

}