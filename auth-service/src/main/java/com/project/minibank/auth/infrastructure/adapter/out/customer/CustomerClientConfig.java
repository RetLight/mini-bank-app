package com.project.minibank.auth.infrastructure.adapter.out.customer;

import com.project.minibank.auth.domain.exception.CustomerAlreadyExistsException;
import com.project.minibank.auth.infrastructure.adapter.out.security.ServiceTokenIssuer;
import com.project.minibank.common.servicecall.ServiceCallFeignLogger;
import com.project.minibank.common.servicecall.ServiceCallHeaders;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

// SIN @Configuration a propósito: solo se aplica a CustomerClient (Feign crea un contexto hijo para él).
class CustomerClientConfig {

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

    // En el registro todavía no hay usuario: auth-service se identifica con su propio token de servicio.
    @Bean
    RequestInterceptor serviceToken(ServiceTokenIssuer tokens) {
        return template -> template.header(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.issue());
    }

    // No lee el cuerpo del error: puede no ser JSON (una página de un proxy, un 502 vacío...).
    @Bean
    ErrorDecoder customerErrorDecoder() {
        return (String methodKey, Response response) -> response.status() == 409
                ? new CustomerAlreadyExistsException()
                : new CustomerCallFailedException("customer-service respondió " + response.status());
    }

    static class CustomerCallFailedException extends RuntimeException {
        CustomerCallFailedException(String message) {
            super(message);
        }
    }
}
