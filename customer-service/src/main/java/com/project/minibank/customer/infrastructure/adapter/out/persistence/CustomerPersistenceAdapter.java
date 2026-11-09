package com.project.minibank.customer.infrastructure.adapter.out.persistence;

import com.project.minibank.customer.application.port.out.CustomerRepositoryPort;
import com.project.minibank.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import com.project.minibank.customer.domain.exception.CustomerAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

@Component
public class CustomerPersistenceAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository customerJpaRepository;

    public CustomerPersistenceAdapter(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        return customerJpaRepository.findWithFavoritesById(id).map(FavoritePersistenceMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return customerJpaRepository.findAllByOrderByIdAsc().stream()
                .map(FavoritePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByDocumentNumberOrEmail(String documentNumber, String email) {
        return customerJpaRepository.existsByDocumentNumberOrEmailIgnoreCase(documentNumber, email);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = new CustomerEntity(customer.getDocumentType(), customer.getDocumentNumber(),
                customer.getFirstNames(), customer.getLastNames(), customer.getEmail(), customer.getPhone(),
                customer.getStatus(), customer.getRegisteredAt());
        try {
            return FavoritePersistenceMapper.toDomain(customerJpaRepository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException e) {
            // Las restricciones UNIQUE son la garantía real: dos altas simultáneas pueden pasar la comprobación previa.
            throw new CustomerAlreadyExistsException();
        }
    }
}
