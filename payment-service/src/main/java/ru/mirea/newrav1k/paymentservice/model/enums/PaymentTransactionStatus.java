package ru.mirea.newrav1k.paymentservice.model.enums;

import lombok.Getter;

@Getter
public enum PaymentTransactionStatus {
    PENDING, SUCCESS, FAILED, CANCELLED;

    public static PaymentTransactionStatus fromString(String status) {
        for (PaymentTransactionStatus transactionStatus : PaymentTransactionStatus.values()) {
            if (transactionStatus.name().equalsIgnoreCase(status)) {
                return transactionStatus;
            }
        }
        throw new IllegalArgumentException("Invalid payment transaction status: " + status);
    }

}