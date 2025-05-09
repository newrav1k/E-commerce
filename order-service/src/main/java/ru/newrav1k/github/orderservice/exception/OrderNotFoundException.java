package ru.newrav1k.github.orderservice.exception;

import java.io.Serial;

public class OrderNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 5564315926595113046L;

    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(Throwable cause) {
        super(cause);
    }

    protected OrderNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}