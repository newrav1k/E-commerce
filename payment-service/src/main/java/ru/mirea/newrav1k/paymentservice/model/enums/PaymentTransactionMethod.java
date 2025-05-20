package ru.mirea.newrav1k.paymentservice.model.enums;

import lombok.Getter;

@Getter
public enum PaymentTransactionMethod {
    CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER;

    public static PaymentTransactionMethod fromString(String method) {
        for (PaymentTransactionMethod transactionMethod : PaymentTransactionMethod.values()) {
            if (transactionMethod.toString().equalsIgnoreCase(method)) {
                return transactionMethod;
            }
        }
        throw new IllegalArgumentException("Invalid payment transaction method: " + method);
    }

}