package com.ndash.idsphere.integrations.exception;

public class ExternalServiceException extends IntegrationException {
    public ExternalServiceException(String code, String message) {
        super(code, message);
    }
}