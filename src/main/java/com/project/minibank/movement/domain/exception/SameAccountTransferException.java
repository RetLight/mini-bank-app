package com.project.minibank.movement.domain.exception;

import com.project.minibank.exception.BusinessRuleException;

public class SameAccountTransferException extends BusinessRuleException {
    public SameAccountTransferException(Long accountId) {
        super("No puedes transferir a la misma cuenta con id " + accountId);
    }
}
