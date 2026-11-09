package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import com.project.minibank.movement.domain.model.Account;

final class AccountPersistenceMapper {

    private AccountPersistenceMapper() {
    }

    static Account toDomain(AccountEntity entity){
        return new Account(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getCci(),
                entity.getType(),
                entity.getCurrency(),
                entity.getBalance(),
                entity.getStatus(),
                entity.getOpenedAt(),
                entity.getCustomerId()
        );
    }

    static AccountEntity toEntity(Account account){
        return new AccountEntity(
                account.getId(),
                account.getAccountNumber(),
                account.getCci(),
                account.getType(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus(),
                account.getOpenedAt(),
                account.getCustomerId()
        );
    }
}
