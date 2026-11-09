package com.project.minibank.customer.domain.exception;

import com.project.minibank.common.exception.ConflictException;

public class CustomerAlreadyExistsException extends ConflictException {
    public CustomerAlreadyExistsException() {
        super("Ya existe un cliente con ese documento o ese email");
    }
}
