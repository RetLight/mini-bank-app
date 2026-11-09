package com.project.minibank.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

// Convierte cualquier fallo dentro del gateway (ruta inexistente, servicio caído, timeout) al formato común.
// @Order(-2) lo pone antes del manejador por defecto de Spring Boot (-1).
@Order(-2)
@Component
class GatewayErrorHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GatewayErrorHandler.class);

    private final ErrorBodies errors;

    GatewayErrorHandler(ErrorBodies errors) {
        this.errors = errors;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = statusOf(ex);
        if (status.is5xxServerError()) {
            log.warn("{} {} falló: {}", exchange.getRequest().getMethod(), exchange.getRequest().getPath(), ex.toString());
        }
        return errors.write(exchange, status, switch (status) {
            case SERVICE_UNAVAILABLE -> "El servicio no está disponible";
            case GATEWAY_TIMEOUT -> "El servicio tardó demasiado en responder";
            case NOT_FOUND -> "No existe esa ruta";
            default -> "Error interno del gateway";
        });
    }

    private static HttpStatus statusOf(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            // El servicio no escucha o su nombre no resuelve: en los dos casos no está disponible.
            if (t instanceof ConnectException || t instanceof UnknownHostException) {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }
            if (t instanceof TimeoutException) {
                return HttpStatus.GATEWAY_TIMEOUT;
            }
            if (t instanceof ResponseStatusException rse) {
                HttpStatus status = HttpStatus.resolve(rse.getStatusCode().value());
                if (status != null) {
                    return status;
                }
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
