package ru.mirea.newrav1k.paymentservice.model.enums;

import lombok.Getter;

@Getter
public enum PaymentTransactionMethod {
    CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER;

    public static PaymentTransactionMethod fromString(String method) {
        for (PaymentTransactionStatus status : PaymentTransactionStatus.values()) {
            if (status.toString().equalsIgnoreCase(method)) {
                return PaymentTransactionMethod.valueOf(method);
            }
        }
        throw new IllegalArgumentException("Invalid payment transaction method: " + method);
    }

}