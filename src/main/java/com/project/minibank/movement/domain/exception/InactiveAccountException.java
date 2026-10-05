package com.project.minibank.movement.domain.exception;

import com.project.minibank.exception.BusinessRuleException;

public class InactiveAccountException extends BusinessRuleException {
    public InactiveAccountException(Long id, String status) {

        super("La cuenta con id " + id + " se encuentra " + status);
    }
}
