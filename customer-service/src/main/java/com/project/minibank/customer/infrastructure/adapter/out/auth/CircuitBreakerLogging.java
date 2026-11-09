package com.project.minibank.customer.infrastructure.adapter.out.auth;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// Deja en el log cada cambio de estado del circuito hacia auth-service (CLOSED -> OPEN -> HALF_OPEN -> CLOSED).
@Component
class CircuitBreakerLogging {

    private static final Logger log = LoggerFactory.getLogger("minibank.servicecall");

    CircuitBreakerLogging(CircuitBreakerRegistry registry) {
        registry.circuitBreaker("authService").getEventPublisher().onStateTransition(event -> log.warn(
                "[customer-service -> auth-service] circuito {}: {}", event.getCircuitBreakerName(),
                event.getStateTransition()));
    }
}
