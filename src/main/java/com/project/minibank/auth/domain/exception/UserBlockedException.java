package com.project.minibank.auth.domain.exception;

public class UserBlockedException extends RuntimeException {

    public UserBlockedException() {
        super("El usuario está bloqueado");
    }
}
