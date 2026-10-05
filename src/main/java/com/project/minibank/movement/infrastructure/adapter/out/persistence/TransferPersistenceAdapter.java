package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import com.project.minibank.movement.application.port.out.TransferRepositoryPort;
import com.project.minibank.movement.domain.model.Transfer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TransferPersistenceAdapter implements TransferRepositoryPort {
    private final TransferJpaRepository transferJpaRepository;
    private final AccountJpaRepository accountJpaRepository;

    public TransferPersistenceAdapter(TransferJpaRepository transferJpaRepository,
                                      AccountJpaRepository accountJpaRepository) {
        this.transferJpaRepository = transferJpaRepository;
        this.accountJpaRepository = accountJpaRepository;
    }


    @Override
    public Transfer save(Transfer transfer) {
        TransferEntity entity = TransferPersistenceMapper.toEntity(transfer);
        transferJpaRepository.save(entity);
        return TransferPersistenceMapper.toDomain(entity);
    }

    @Override
    public Optional<Transfer> findById(Long customerId, Long id) {
        return transferJpaRepository.findById(id)
                .filter(transfer -> belongsToCustomer(transfer.getSourceAccountId(), customerId)
                        || belongsToCustomer(transfer.getDestinationAccountId(), customerId))
                .map(TransferPersistenceMapper::toDomain);
    }

    @Override
    public List<Transfer> findAllByAccount(Long customerId, Long accountId) {
        if (accountJpaRepository.findByIdAndCustomerId(accountId, customerId).isEmpty()) {
            return List.of();
        }
        return transferJpaRepository.findAllBySourceAccountId(accountId).stream()
                .map(TransferPersistenceMapper::toDomain)
                .toList();
    }

    private boolean belongsToCustomer(Long accountId, Long customerId) {
        return accountId != null && accountJpaRepository.findByIdAndCustomerId(accountId, customerId).isPresent();
    }
}
