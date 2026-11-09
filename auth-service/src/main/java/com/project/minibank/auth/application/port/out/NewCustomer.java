package com.project.minibank.auth.application.port.out;

// Datos del cliente que se crea en customer-service durante el registro.
public record NewCustomer(String documentType, String documentNumber, String firstNames, String lastNames,
                          String email, String phone) {
}
