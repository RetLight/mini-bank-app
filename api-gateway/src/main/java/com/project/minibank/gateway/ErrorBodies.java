package com.project.minibank.gateway;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

// El gateway responde con el mismo ErrorResponse que los servicios (status, message, details, timestamp),
// así el cliente interpreta un único formato de error, lo produzca quien lo produzca.
// JsonMapper es el de Jackson 3 (Spring Boot 4): en Jackson 3 las excepciones no son obligatorias de capturar,
// pero se conserva un JSON mínimo de respaldo por si la serialización fallara.
@Component
class ErrorBodies {

    record ErrorResponse(int status, String message, List<String> details, LocalDateTime timestamp) {
    }

    private final JsonMapper mapper;

    ErrorBodies(JsonMapper mapper) {
        this.mapper = mapper;
    }

    Mono<Void> write(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.empty();
        }
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] json;
        try {
            json = mapper.writeValueAsBytes(new ErrorResponse(status.value(), message, List.of(), LocalDateTime.now()));
        } catch (JacksonException e) {
            json = ("{\"status\":" + status.value() + "}").getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = response.bufferFactory().wrap(json);
        return response.writeWith(Mono.just(buffer));
    }
}
