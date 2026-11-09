package com.project.minibank.customer.infrastructure.adapter.in.web;

import com.project.minibank.customer.domain.model.Customer;

import java.time.LocalDateTime;

public record CustomerResponse(Long id, String documentType, String documentNumber, String firstNames, String lastNames,
                               String email, String phone, String status, LocalDateTime registeredAt,
                               int favoritesCount) {

    static CustomerResponse from(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getDocumentType(), customer.getDocumentNumber(),
                customer.getFirstNames(), customer.getLastNames(), customer.getEmail(), customer.getPhone(),
                customer.getStatus().name(), customer.getRegisteredAt(), customer.getFavorites().size());
    }
}
