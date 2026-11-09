package com.project.minibank.auth.domain.exception;

import com.project.minibank.common.exception.ConflictException;

public class UsernameAlreadyUsedException extends ConflictException {
    public UsernameAlreadyUsedException(String username) {
        super("El usuario " + username + " ya existe");
    }
}
