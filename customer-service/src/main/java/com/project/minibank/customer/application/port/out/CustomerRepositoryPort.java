package com.project.minibank.customer.application.port.out;

import com.project.minibank.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {

    Optional<Customer> findById(Long id);

    List<Customer> findAll();

    boolean existsByDocumentNumberOrEmail(String documentNumber, String email);

    Customer save(Customer customer);
}
