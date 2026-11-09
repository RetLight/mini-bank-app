package com.project.minibank.customer.infrastructure.adapter.out.auth;

/**
 * auth-service respondió un 4xx distinto de 404 (por ejemplo, 401 al token reenviado). Está vivo, así que no debe
 * abrir el circuito ni reintentarse: preguntar otra vez con el mismo token da la misma respuesta.
 */
public class AuthRejectedException extends RuntimeException {

    public AuthRejectedException(int status) {
        super("auth-service rechazó la consulta con " + status);
    }
}
