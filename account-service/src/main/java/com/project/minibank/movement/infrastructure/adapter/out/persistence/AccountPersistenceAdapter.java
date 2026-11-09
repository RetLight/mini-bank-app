package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import com.project.minibank.movement.application.port.out.AccountRepositoryPort;
import com.project.minibank.movement.domain.model.Account;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AccountPersistenceAdapter implements AccountRepositoryPort {
    private final AccountJpaRepository accountJpaRepository;

    public AccountPersistenceAdapter(AccountJpaRepository accountJpaRepository) {
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Account saveBalance(Account account) {
        AccountEntity saved = accountJpaRepository.save(AccountPersistenceMapper.toEntity(account));
        return AccountPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(Long customerId, Long id) {
        return accountJpaRepository.findByIdAndCustomerId(id, customerId)
                .map(AccountPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Account> findByCci(String cci) {
        return accountJpaRepository.findByCci(cci)
                .map(AccountPersistenceMapper::toDomain);
    }
}
