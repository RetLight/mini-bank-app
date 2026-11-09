package com.project.minibank.customer.application.port.in;

public record RegisterCustomerCommand(String documentType, String documentNumber, String firstNames, String lastNames,
                                      String email, String phone) {
}
