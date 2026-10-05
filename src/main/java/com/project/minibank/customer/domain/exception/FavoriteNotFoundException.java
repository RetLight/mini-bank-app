package com.project.minibank.customer.domain.exception;

import com.project.minibank.exception.ResourceNotFoundException;

public class FavoriteNotFoundException extends ResourceNotFoundException {
    public FavoriteNotFoundException(Long customerId, Long id) {
        super("Registro no encontrado con id " + id + " para el cliente con id " + customerId);
    }
}
