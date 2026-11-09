package com.project.minibank.movement.application.port.out;

import com.project.minibank.movement.domain.model.Movement;

import java.util.List;
import java.util.Optional;

public interface MovementRepositoryPort {
    Movement save(Movement movement);
    Optional<Movement> findById(Long customerId, Long id);
    List<Movement> findAllByAccount(Long customerId, Long accountId);
}
