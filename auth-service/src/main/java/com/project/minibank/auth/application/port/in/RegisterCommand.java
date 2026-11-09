package com.project.minibank.auth.application.port.in;

public record RegisterCommand(String username, String password, String documentType, String documentNumber,
                              String firstNames, String lastNames, String email, String phone) {
}
