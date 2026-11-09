package com.project.minibank.movement.application.port.in;

import com.project.minibank.movement.domain.model.Movement;

import java.util.List;

public interface GetMovementUseCase {
    Movement findById(Long customerId, Long id);
    List<Movement> findAllByAccount(Long customerId, Long accountId);
}
