package com.project.minibank.customer.application.port.out;

/**
 * Las cuatro respuestas posibles a "¿el usuario de este cliente sigue activo?". Que Unavailable sea explícito importa:
 * "no existe" y "no pude preguntar" son cosas distintas y piden reacciones distintas.
 */
public sealed interface UserStatus {

    record Active() implements UserStatus {
    }

    record Blocked() implements UserStatus {
    }

    /** El usuario ya no existe en auth-service. */
    record NotFound() implements UserStatus {
    }

    /** Sin respuesta fiable (auth-service caído, lento, fallando o con el circuito abierto). No dice nada del usuario. */
    record Unavailable(String reason) implements UserStatus {
    }
}
