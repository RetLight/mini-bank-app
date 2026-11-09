package com.project.minibank.customer.application.service;

import com.project.minibank.customer.application.port.in.ListCustomersUseCase;
import com.project.minibank.customer.application.port.out.CustomerRepositoryPort;
import com.project.minibank.customer.domain.model.Customer;
import org.springframework.stereotype.Service;

import java.util.List;

// Casos de uso de administración. Quién puede usarlos (rol ADMIN) lo decide la seguridad, no este servicio.
@Service
public class CustomerAdminService implements ListCustomersUseCase {

    private final CustomerRepositoryPort customerRepository;

    public CustomerAdminService(CustomerRepositoryPort customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
}
