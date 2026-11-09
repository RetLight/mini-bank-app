package com.project.minibank.customer.infrastructure.adapter.out.auth;

/**
 * auth-service falló de una forma que no dice nada del usuario: 5xx, conexión rechazada, timeout o conexión cortada.
 * Es la ÚNICA excepción que el Circuit Breaker cuenta como fallo y la que Retry repite (ver config-repo).
 */
public class AuthUnavailableException extends RuntimeException {

    public AuthUnavailableException(String message) {
        super(message);
    }

    public AuthUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
