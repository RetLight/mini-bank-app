package com.project.minibank.movement.domain.exception;

import com.project.minibank.exception.ResourceNotFoundException;

public class AccountNotFoundException extends ResourceNotFoundException {
    public AccountNotFoundException(Long customerId, Long accountId) {

        super("Cuenta no encontrado con id " + accountId + " para el cliente con id " + customerId);
    }
}
