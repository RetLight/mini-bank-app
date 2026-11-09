package com.project.minibank.customer.domain.exception;

import com.project.minibank.common.exception.ForbiddenOperationException;

public class UserBlockedException extends ForbiddenOperationException {
    public UserBlockedException(Long customerId) {
        super("El usuario del cliente " + customerId + " está bloqueado: no puede agregar favoritos");
    }
}
