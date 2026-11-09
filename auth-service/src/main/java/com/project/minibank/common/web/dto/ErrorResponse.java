package com.project.minibank.common.web.dto;

import java.time.LocalDateTime;
import java.util.List;

// El único formato de error de MiniBank: lo usan los tres servicios y también el gateway.
public record ErrorResponse(int status, String message, List<String> details, LocalDateTime timestamp) {

    public static ErrorResponse of(int status, String message, List<String> details) {
        return new ErrorResponse(status, message, details, LocalDateTime.now());
    }
}
