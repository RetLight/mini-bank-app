package com.project.minibank.customer.domain.exception;

import com.project.minibank.common.exception.BusinessRuleException;

public class FavoriteAliasAlreadyUsedException extends BusinessRuleException {

    public FavoriteAliasAlreadyUsedException(String alias) {
        super("Ya usaste el alias '" + alias + "', elige otro");
    }
}
