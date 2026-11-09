package com.project.minibank.movement.application.port.in;

import com.project.minibank.movement.domain.model.Transfer;

import java.util.List;

public interface GetTransferUseCase {
    Transfer findById(Long customerId, Long id);
    List<Transfer> findAllByAccount(Long customerId, Long accountId);
}
