package com.project.minibank.customer.application.port.out;

/** Pregunta por el estado del usuario sin saber de dónde sale la respuesta, y sin lanzar excepciones por fallos esperados. */
public interface UserStatusPort {
    UserStatus statusOf(Long customerId);
}
