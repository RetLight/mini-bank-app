package com.project.minibank.common.exception;

// El recurso ya existe (usuario, documento o email repetidos): 409.
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
