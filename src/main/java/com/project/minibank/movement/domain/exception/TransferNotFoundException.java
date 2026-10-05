package com.project.minibank.movement.domain.exception;

import com.project.minibank.exception.ResourceNotFoundException;

public class TransferNotFoundException extends ResourceNotFoundException {
    public TransferNotFoundException(Long customerId, Long id) {

        super("Transferencia no encontrada con id " + id + " para el cliente con id " + customerId);
    }
}
