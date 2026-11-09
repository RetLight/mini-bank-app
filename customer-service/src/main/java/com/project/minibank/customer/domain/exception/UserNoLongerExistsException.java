package com.project.minibank.customer.domain.exception;

import com.project.minibank.common.exception.ResourceNotFoundException;

public class UserNoLongerExistsException extends ResourceNotFoundException {
    public UserNoLongerExistsException(Long customerId) {
        super("El usuario del cliente " + customerId + " ya no existe");
    }
}
