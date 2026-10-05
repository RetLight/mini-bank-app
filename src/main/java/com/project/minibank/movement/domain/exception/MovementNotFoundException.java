package com.project.minibank.movement.domain.exception;

import com.project.minibank.exception.ResourceNotFoundException;

public class MovementNotFoundException extends ResourceNotFoundException {
    public MovementNotFoundException(Long customerId, Long id) {

        super("Movimiento no encontrado con id " + id + " para el cliente con id " + customerId);
    }
}
