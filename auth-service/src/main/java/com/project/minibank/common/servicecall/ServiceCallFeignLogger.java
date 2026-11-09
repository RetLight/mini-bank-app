package com.project.minibank.common.servicecall;

import feign.Logger;
import feign.Request;
import feign.Response;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Una línea de log por cada llamada que este servicio hace a otro, con Feign: a quién, qué, con qué resultado y
 * cuánto tardó. El servicio que recibe la llamada escribe la suya (IncomingServiceCallFilter), así se ve el salto
 * desde los dos lados. Ejemplo:
 *   [auth-service -> customer-service] POST /internal/customers [req=8f1c...]
 *   [auth-service <- customer-service] 201 en 35 ms (POST /internal/customers)
 */
public class ServiceCallFeignLogger extends Logger {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger("minibank.servicecall");

    private final String caller;

    public ServiceCallFeignLogger(String caller) {
        this.caller = caller;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        log.info("[{} -> {}] {} {} [req={}]", caller, target(request), request.httpMethod(), path(request.url()),
                first(request, ServiceCallHeaders.REQUEST_ID));
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) {
        Request request = response.request();
        log.info("[{} <- {}] {} en {} ms ({} {})", caller, target(request), response.status(), elapsedTime,
                request.httpMethod(), path(request.url()));
        return response;
    }

    @Override
    protected IOException logIOException(String configKey, Level logLevel, IOException ioe, long elapsedTime) {
        log.warn("[{} -x {}] sin respuesta tras {} ms: {}", caller, configKey, elapsedTime, ioe.toString());
        return ioe;
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        log.debug(configKey + " " + String.format(format, args));
    }

    private static String target(Request request) {
        var template = request.requestTemplate();
        return template != null && template.feignTarget() != null ? template.feignTarget().name() : URI.create(request.url()).getHost();
    }

    private static String path(String url) {
        return URI.create(url).getRawPath();
    }

    private static String first(Request request, String header) {
        var values = request.headers().get(header);
        return values == null || values.isEmpty() ? "-" : values.iterator().next();
    }
}
