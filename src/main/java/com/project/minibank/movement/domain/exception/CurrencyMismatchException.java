package com.project.minibank.movement.domain.exception;

import com.project.minibank.exception.BusinessRuleException;

public class CurrencyMismatchException extends BusinessRuleException {
    public CurrencyMismatchException(String sourceCurrency, String destinationCurrency) {
        super("La moneda de la cuenta origen (" + sourceCurrency + ") no coincide con la de la cuenta destino ("
                + destinationCurrency + ")");
    }
}
