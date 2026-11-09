package com.project.minibank.customer.domain.exception;

import com.project.minibank.common.exception.ResourceNotFoundException;

public class FavoriteNotFoundByAliasException extends ResourceNotFoundException {
    public FavoriteNotFoundByAliasException(Long customerId, String alias) {
        super("Registro no encontrado con alias " + alias + " para el cliente con id " + customerId);
    }
}
