package com.project.minibank.customer.infrastructure.adapter.out.auth;

import com.project.minibank.customer.application.port.out.UserStatus;
import com.project.minibank.customer.application.port.out.UserStatusPort;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Orden de los aspectos (lo fija Resilience4j): Retry( CircuitBreaker( llamada ) ). El fallback va en @Retry, el aspecto
// MÁS EXTERNO: si estuviera en @CircuitBreaker se tragaría la excepción y Retry nunca vería un fallo que reintentar.
// Las dos anotaciones funcionan porque FavoriteService llama a este bean a través del proxy de Spring
// (una llamada this.metodo() se las saltaría).
@Component
class UserStatusFeignAdapter implements UserStatusPort {

    private static final Logger log = LoggerFactory.getLogger(UserStatusFeignAdapter.class);

    private final AuthClient authClient;

    UserStatusFeignAdapter(AuthClient authClient) {
        this.authClient = authClient;
    }

    // Un GET es idempotente: reintentarlo es seguro. Nunca se pone @Retry en una llamada que crea algo.
    @Override
    @Retry(name = "authService", fallbackMethod = "unavailable")
    @CircuitBreaker(name = "authService")
    public UserStatus statusOf(Long customerId) {
        try {
            AuthClient.UserStatusResponse me = authClient.me();
            if (!customerId.equals(me.customerId())) {
                // El token dice un cliente y auth-service otro: no se puede confiar en ninguno de los dos.
                log.warn("auth-service asocia el usuario {} al cliente {}, no al {}", me.username(), me.customerId(), customerId);
                return new UserStatus.NotFound();
            }
            return me.blocked() ? new UserStatus.Blocked() : new UserStatus.Active();
        } catch (AuthClientConfig.AuthUserNotFoundException e) {
            return new UserStatus.NotFound();
        } catch (FeignException e) {
            // Conexión rechazada, timeout, conexión cortada o cuerpo ilegible: no sabemos si el usuario existe.
            throw new AuthUnavailableException("auth-service no responde", e);
        }
    }

    // Se llega aquí con los reintentos agotados, el circuito abierto o auth-service rechazando la consulta.
    // No inventa un usuario: solo dice "no pude comprobarlo". Qué significa eso para el negocio lo decide FavoriteService.
    UserStatus unavailable(Long customerId, Throwable cause) {
        log.warn("[customer-service -x auth-service] fallback para el cliente {}: {}", customerId, cause.toString());
        return new UserStatus.Unavailable(cause.getClass().getSimpleName());
    }
}
