package com.project.minibank.auth.domain.exception;

import com.project.minibank.common.exception.ConflictException;

// customer-service respondió 409: ya hay un cliente con ese documento o email.
public class CustomerAlreadyExistsException extends ConflictException {
    public CustomerAlreadyExistsException() {
        super("Ya existe un cliente con ese documento o ese email");
    }
}
