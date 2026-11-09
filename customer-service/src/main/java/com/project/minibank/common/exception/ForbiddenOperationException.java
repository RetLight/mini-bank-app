package com.project.minibank.common.exception;

// Sabemos quién es, pero su estado no le permite hacer esto (por ejemplo, un usuario bloqueado): 403.
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}
