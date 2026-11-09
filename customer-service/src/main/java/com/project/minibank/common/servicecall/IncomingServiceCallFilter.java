package com.project.minibank.common.servicecall;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * Lado que recibe: una línea de log cuando la petición viene de otro servicio (cabecera X-Caller-Service).
 * Ejemplo: [customer-service <- auth-service] POST /internal/customers -> 201 en 12 ms [req=8f1c...]
 * Las peticiones de clientes (sin esa cabecera) no se registran aquí: de eso se encarga el gateway.
 */
@Component
public class IncomingServiceCallFilter extends OncePerRequestFilter {

    // Las mismas cabeceras que pone ServiceCallHeaders en el servicio que llama.
    static final String CALLER = "X-Caller-Service";
    static final String REQUEST_ID = "X-Request-Id";

    private static final Logger log = LoggerFactory.getLogger("minibank.servicecall");

    private final String self;

    public IncomingServiceCallFilter(@Value("${spring.application.name}") String self) {
        this.self = self;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getHeader(CALLER) == null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            log.info("[{} <- {}] {} {} -> {} en {} ms [req={}]", self, request.getHeader(CALLER),
                    request.getMethod(), request.getRequestURI(), response.getStatus(),
                    (System.nanoTime() - start) / 1_000_000, Objects.toString(request.getHeader(REQUEST_ID), "-"));
        }
    }
}
