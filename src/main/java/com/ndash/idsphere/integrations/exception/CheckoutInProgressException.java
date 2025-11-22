package com.ndash.idsphere.integrations.exception;

public class CheckoutInProgressException extends RuntimeException {
    public CheckoutInProgressException(String message) {
        super(message);
    }
}
