package com.project.minibank.customer.infrastructure.adapter.out.auth;

import com.project.minibank.common.servicecall.ServiceCallFeignLogger;
import com.project.minibank.common.servicecall.ServiceCallHeaders;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// SIN @Configuration a propósito: solo se aplica a AuthClient (Feign crea un contexto hijo para él).
class AuthClientConfig {

    /** auth-service respondió 404: el usuario ya no existe. Señal interna entre el decoder y el adaptador. */
    static class AuthUserNotFoundException extends RuntimeException {
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    Logger feignLogger(@Value("${spring.application.name}") String self) {
        return new ServiceCallFeignLogger(self);
    }

    @Bean
    RequestInterceptor serviceCallHeaders(@Value("${spring.application.name}") String self) {
        return new ServiceCallHeaders(self);
    }

    // La llamada se hace en nombre del usuario: su token viaja con ella, y auth-service responde solo por él.
    @Bean
    RequestInterceptor forwardUserToken() {
        return template -> {
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
                String authorization = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
                if (authorization != null) {
                    template.header(HttpHeaders.AUTHORIZATION, authorization);
                }
            }
        };
    }

    // No lee el cuerpo del error: puede no ser JSON (una página de un proxy, un 502 vacío...).
    @Bean
    ErrorDecoder authErrorDecoder() {
        return (String methodKey, Response response) -> {
            int status = response.status();
            if (status == 404) {
                return new AuthUserNotFoundException();                                   // una respuesta, no un fallo
            }
            return status >= 500
                    ? new AuthUnavailableException("auth-service respondió " + status)  // un fallo: cuenta y se reintenta
                    : new AuthRejectedException(status);                                // vivo pero rechaza: ni lo uno ni lo otro
        };
    }
}
