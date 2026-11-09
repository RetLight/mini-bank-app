package com.project.minibank.movement.application.port.out;

import com.project.minibank.movement.domain.model.Account;

import java.util.Optional;

public interface AccountRepositoryPort {
    Account saveBalance(Account account);
    Optional<Account> findById(Long customerId, Long id);
    Optional<Account> findByCci(String cci);
}
