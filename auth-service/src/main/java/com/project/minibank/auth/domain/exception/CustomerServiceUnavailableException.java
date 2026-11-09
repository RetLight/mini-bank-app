package com.project.minibank.auth.domain.exception;

// customer-service no respondió o falló: sin él no se puede crear el cliente, así que el registro no sigue (503).
public class CustomerServiceUnavailableException extends RuntimeException {
    public CustomerServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
