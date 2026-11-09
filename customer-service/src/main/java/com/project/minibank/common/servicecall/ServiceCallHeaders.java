package com.project.minibank.common.servicecall;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

/**
 * Cabeceras de toda llamada entre servicios: X-Caller-Service (quién llama, para el log del que recibe) y
 * X-Request-Id (el de la petición que la originó, para seguirla de punta a punta; si no hay, se genera uno).
 * El gateway borra X-Caller-Service de las peticiones externas: un cliente no puede fingir ser un servicio.
 */
public class ServiceCallHeaders implements RequestInterceptor {

    public static final String CALLER = "X-Caller-Service";
    public static final String REQUEST_ID = "X-Request-Id";

    private final String caller;

    public ServiceCallHeaders(String caller) {
        this.caller = caller;
    }

    @Override
    public void apply(RequestTemplate template) {
        template.header(CALLER, caller);
        String requestId = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            requestId = attributes.getRequest().getHeader(REQUEST_ID);
        }
        template.header(REQUEST_ID, requestId != null ? requestId : UUID.randomUUID().toString());
    }
}
