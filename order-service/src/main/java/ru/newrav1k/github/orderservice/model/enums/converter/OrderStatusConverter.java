package ru.newrav1k.github.orderservice.model.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.newrav1k.github.orderservice.model.enums.OrderStatus;

@Converter(autoApply = true)
public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {

    @Override
    public String convertToDatabaseColumn(OrderStatus attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String dbData) {
        return dbData == null ? null : OrderStatus.fromString(dbData);
    }

}