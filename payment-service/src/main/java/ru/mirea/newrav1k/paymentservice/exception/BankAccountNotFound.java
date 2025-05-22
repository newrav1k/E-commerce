package ru.mirea.newrav1k.paymentservice.exception;

import java.io.Serial;

public class BankAccountNotFound extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8998154403230741184L;

    public BankAccountNotFound() {
        super();
    }

    public BankAccountNotFound(String message) {
        super(message);
    }

    public BankAccountNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public BankAccountNotFound(Throwable cause) {
        super(cause);
    }

    protected BankAccountNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}