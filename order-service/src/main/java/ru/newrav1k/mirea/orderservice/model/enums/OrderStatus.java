package ru.newrav1k.mirea.orderservice.model.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING, APPROVED, REJECTED, PAID, CANCELLED;

    public static OrderStatus fromString(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.toString().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + status);
    }

}