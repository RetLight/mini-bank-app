package com.project.minibank.movement.domain.exception;

import com.project.minibank.exception.BusinessRuleException;

public class InsufficientAccountBalanceException extends BusinessRuleException {
    public InsufficientAccountBalanceException(Long id) {

        super("Saldo insuficiente en la cuenta con id " + id);
    }
}
