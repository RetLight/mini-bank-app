package com.project.minibank.movement.application.service;

import com.project.minibank.movement.application.port.in.GetMovementUseCase;
import com.project.minibank.movement.application.port.out.MovementRepositoryPort;
import com.project.minibank.movement.domain.exception.MovementNotFoundException;
import com.project.minibank.movement.domain.model.Movement;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovementService implements GetMovementUseCase {
    private final MovementRepositoryPort movementRepositoryPort;

    public MovementService(MovementRepositoryPort movementRepositoryPort) {
        this.movementRepositoryPort = movementRepositoryPort;
    }

    @Override
    public Movement findById(Long customerId, Long id) {
        return movementRepositoryPort.findById(customerId,id)
                .orElseThrow(() -> new MovementNotFoundException(customerId,id));
    }

    @Override
    public List<Movement> findAllByAccount(Long customerId, Long accountId) {
        return movementRepositoryPort.findAllByAccount(customerId, accountId);
    }
}
