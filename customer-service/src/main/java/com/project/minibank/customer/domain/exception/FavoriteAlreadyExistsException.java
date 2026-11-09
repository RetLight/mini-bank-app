package com.project.minibank.customer.domain.exception;

import com.project.minibank.common.exception.BusinessRuleException;

public class FavoriteAlreadyExistsException extends BusinessRuleException {

    public FavoriteAlreadyExistsException(String accountNumber, String bank) {
        super("Ya tienes guardada la cuenta " + accountNumber + " del banco " + bank + " como favorito");
    }
}
