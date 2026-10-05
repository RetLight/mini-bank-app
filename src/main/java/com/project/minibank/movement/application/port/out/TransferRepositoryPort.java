package com.project.minibank.movement.application.port.out;

import com.project.minibank.movement.domain.model.Transfer;

import java.util.List;
import java.util.Optional;

public interface TransferRepositoryPort {
    Transfer save(Transfer transfer);
    Optional<Transfer> findById(Long customerId, Long id);
    List<Transfer> findAllByAccount(Long customerId, Long accountId);
}
