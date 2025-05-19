package ru.mirea.newrav1k.productservice.exception;

import java.io.Serial;

public class InventoryNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2832826276391931984L;

    public InventoryNotFoundException() {
        super();
    }

    public InventoryNotFoundException(String message) {
        super(message);
    }

    public InventoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InventoryNotFoundException(Throwable cause) {
        super(cause);
    }

    protected InventoryNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}