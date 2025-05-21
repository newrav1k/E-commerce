package ru.newrav1k.mirea.orderservice.exception;

import java.io.Serial;

public class ProductClientException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 9073570003438975327L;

    public ProductClientException(String message) {
        super(message);
    }

}