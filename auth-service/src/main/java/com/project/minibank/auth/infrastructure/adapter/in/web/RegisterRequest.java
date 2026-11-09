package com.project.minibank.auth.infrastructure.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// La contraseña tiene un máximo de 72 caracteres: BCrypt ignora lo que pasa de 72 bytes.
public record RegisterRequest(
        @NotBlank(message = "El usuario es obligatorio")
        @Pattern(regexp = "[A-Za-z0-9._-]{3,50}", message = "El usuario debe tener entre 3 y 50 letras, dígitos, '.', '_' o '-'")
        String username,
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,
        @NotBlank(message = "El tipo de documento es obligatorio") @Size(max = 10) String documentType,
        @NotBlank(message = "El número de documento es obligatorio")
        @Pattern(regexp = "\\d{8,20}", message = "El número de documento debe tener entre 8 y 20 dígitos")
        String documentNumber,
        @NotBlank(message = "Los nombres son obligatorios") @Size(max = 100) String firstNames,
        @NotBlank(message = "Los apellidos son obligatorios") @Size(max = 100) String lastNames,
        @NotBlank(message = "El email es obligatorio") @Email(message = "El email no es válido") @Size(max = 150) String email,
        @Pattern(regexp = "\\d{6,20}", message = "El teléfono debe tener entre 6 y 20 dígitos") String phone) {
}
