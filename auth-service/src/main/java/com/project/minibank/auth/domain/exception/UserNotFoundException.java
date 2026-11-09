package com.project.minibank.auth.domain.exception;

import com.project.minibank.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException(String username) {
        super("El usuario " + username + " no existe");
    }
}
