package com.project.minibank.auth.infrastructure.adapter.in.web;

import com.project.minibank.auth.domain.exception.CustomerServiceUnavailableException;
import com.project.minibank.auth.domain.exception.InvalidCredentialsException;
import com.project.minibank.auth.domain.exception.UserBlockedException;
import com.project.minibank.common.web.ErrorResponses;
import com.project.minibank.common.web.dto.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Errores propios del área auth. Precedencia máxima: se consulta antes que la red de seguridad de GlobalExceptionHandler.
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ErrorResponses.build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UserBlockedException.class)
    public ResponseEntity<ErrorResponse> handleUserBlocked(UserBlockedException ex) {
        return ErrorResponses.build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(CustomerServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleCustomerServiceUnavailable(CustomerServiceUnavailableException ex) {
        return ErrorResponses.build(HttpStatus.SERVICE_UNAVAILABLE,
                "No se puede completar el registro en este momento; inténtalo más tarde");
    }
}
