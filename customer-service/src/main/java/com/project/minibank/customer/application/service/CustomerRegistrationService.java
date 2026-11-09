package com.project.minibank.customer.application.service;

import com.project.minibank.customer.application.port.in.RegisterCustomerCommand;
import com.project.minibank.customer.application.port.in.RegisterCustomerUseCase;
import com.project.minibank.customer.application.port.out.CustomerRepositoryPort;
import com.project.minibank.customer.domain.exception.CustomerAlreadyExistsException;
import com.project.minibank.customer.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// Lo usa auth-service durante el registro (POST /internal/customers): el cliente se crea antes que su usuario.
@Service
public class CustomerRegistrationService implements RegisterCustomerUseCase {

    private final CustomerRepositoryPort customerRepository;

    public CustomerRegistrationService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer register(RegisterCustomerCommand command) {
        if (customerRepository.existsByDocumentNumberOrEmail(command.documentNumber(), command.email())) {
            throw new CustomerAlreadyExistsException();
        }
        return customerRepository.save(Customer.register(command.documentType(), command.documentNumber(),
                command.firstNames(), command.lastNames(), command.email(), command.phone(), LocalDateTime.now()));
    }
}
