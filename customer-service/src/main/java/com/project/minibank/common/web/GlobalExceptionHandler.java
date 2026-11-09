package com.project.minibank.common.web;

import com.project.minibank.common.exception.BusinessRuleException;
import com.project.minibank.common.exception.ConflictException;
import com.project.minibank.common.exception.ForbiddenOperationException;
import com.project.minibank.common.exception.ResourceNotFoundException;
import com.project.minibank.common.web.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

// Aquí solo viven los errores genéricos y las excepciones base del dominio (common.exception). Cada área mapea
// las suyas (por ejemplo AuthExceptionHandler). Todas las respuestas, también las que genera Spring (405, 415,
// ruta inexistente, 500), usan el mismo ErrorResponse.
// Precedencia mínima: los advices se consultan en orden y gana el primero con un manejador que encaje. La red de
// seguridad de Exception tiene que ir después de los manejadores de cada área, o los taparía.
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ErrorResponses.build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException ex) {
        return ErrorResponses.build(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex) {
        return ErrorResponses.build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenOperationException ex) {
        return ErrorResponses.build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).toList();
        return ErrorResponses.build(HttpStatus.BAD_REQUEST, "Datos inválidos", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return ErrorResponses.build(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es válido");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ErrorResponses.build(HttpStatus.BAD_REQUEST, "Valor no válido para el parámetro '" + ex.getName() + "'");
    }

    // A diferencia de TaskHub, el dominio de MiniBank valida sus datos lanzando IllegalArgumentException
    // (por ejemplo, un monto <= 0 o un CCI con letras): por eso sí se responde 400.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidData(IllegalArgumentException ex) {
        return ErrorResponses.build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ErrorResponses.build(HttpStatus.CONFLICT, "El registro entra en conflicto con datos existentes");
    }

    // Red de seguridad.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) throws Exception {
        // Las excepciones de seguridad son de Spring Security: él responde 401 y 403.
        if (ex instanceof AccessDeniedException || ex instanceof AuthenticationException) {
            throw ex;
        }
        // Excepciones del framework que ya conocen su estado: 405, 415, ruta inexistente, parámetro que falta...
        if (ex instanceof org.springframework.web.ErrorResponse framework) {
            HttpStatus status = HttpStatus.valueOf(framework.getStatusCode().value());
            return ErrorResponses.build(status, status.getReasonPhrase());
        }
        log.error("Error inesperado", ex);
        return ErrorResponses.build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");
    }
}
