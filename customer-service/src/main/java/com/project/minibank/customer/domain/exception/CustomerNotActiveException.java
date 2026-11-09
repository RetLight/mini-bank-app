package com.project.minibank.customer.domain.exception;

import com.project.minibank.common.exception.BusinessRuleException;

public class CustomerNotActiveException extends BusinessRuleException {

    public CustomerNotActiveException(Long id) {
        super("El cliente no está activo: " + id);
    }
}
