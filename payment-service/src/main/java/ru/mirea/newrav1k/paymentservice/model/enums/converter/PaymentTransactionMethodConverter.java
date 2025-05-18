package ru.mirea.newrav1k.paymentservice.model.enums.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import ru.mirea.newrav1k.paymentservice.model.enums.PaymentTransactionMethod;

@Converter(autoApply = true)
public class PaymentTransactionMethodConverter implements AttributeConverter<PaymentTransactionMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentTransactionMethod attribute) {
        return attribute == null ? null : attribute.name();
    }

    @Override
    public PaymentTransactionMethod convertToEntityAttribute(String dbData) {
        return dbData == null ? null : PaymentTransactionMethod.fromString(dbData);
    }

}