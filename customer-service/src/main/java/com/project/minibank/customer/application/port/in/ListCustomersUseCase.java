package com.project.minibank.customer.application.port.in;

import com.project.minibank.customer.domain.model.Customer;

import java.util.List;

public interface ListCustomersUseCase {
    List<Customer> findAll();
}
