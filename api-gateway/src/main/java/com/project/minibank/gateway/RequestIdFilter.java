package com.project.minibank.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Identificador de correlación: cada petición sale del gateway con un X-Request-Id, hacia el servicio y de vuelta
 * al cliente, y una línea de log por petición une ese id con el resultado. También quita X-Caller-Service:
 * esa cabecera la ponen los servicios cuando se llaman entre sí, y un cliente externo no debe poder fingirla.
 */
@Component
class RequestIdFilter implements GlobalFilter, Ordered {

    static final String REQUEST_ID = "X-Request-Id";
    static final String CALLER = "X-Caller-Service";

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);
    // El id del cliente solo se reutiliza si es inofensivo (acaba en los logs); si no, se genera uno nuevo.
    private static final Pattern SAFE_ID = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String incoming = exchange.getRequest().getHeaders().getFirst(REQUEST_ID);
        String id = incoming != null && SAFE_ID.matcher(incoming).matches() ? incoming : UUID.randomUUID().toString();

        exchange.getResponse().getHeaders().set(REQUEST_ID, id);
        var request = exchange.getRequest().mutate()
                .headers(h -> {
                    h.set(REQUEST_ID, id);
                    h.remove(CALLER);
                })
                .build();
        long start = System.nanoTime();

        return chain.filter(exchange.mutate().request(request).build()).doFinally(signal -> log.info(
                "{} {} -> {} en {} ms [{}]", request.getMethod(), request.getURI().getPath(),
                exchange.getResponse().getStatusCode(), (System.nanoTime() - start) / 1_000_000, id));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
