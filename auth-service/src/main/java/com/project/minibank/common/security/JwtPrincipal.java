package com.project.minibank.common.security;

/**
 * Identidad de quien llama, construida solo con los claims de un token válido (sin consultar ninguna base).
 * Un servicio que valida tokens no necesita ser dueño de la tabla de usuarios.
 * customerId es null únicamente en el token de servicio (rol SERVICE) que auth-service usa en el registro.
 */
public record JwtPrincipal(String username, Long customerId, String role) {

    public static final String SERVICE = "SERVICE";

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
