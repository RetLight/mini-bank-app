package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import com.project.minibank.movement.application.port.out.MovementRepositoryPort;
import com.project.minibank.movement.domain.model.Movement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class MovementPersistenceAdapter implements MovementRepositoryPort {
    private final MovementJpaRepository movementJpaRepository;
    private final AccountJpaRepository accountJpaRepository;

    public MovementPersistenceAdapter(MovementJpaRepository movementJpaRepository,
                                      AccountJpaRepository accountJpaRepository) {
        this.movementJpaRepository = movementJpaRepository;
        this.accountJpaRepository = accountJpaRepository;
    }

    @Override
    public Movement save(Movement movement) {
        MovementEntity saved = movementJpaRepository.save(MovementPersistenceMapper.toEntity(movement));
        return MovementPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Movement> findById(Long customerId, Long id) {
        return movementJpaRepository.findById(id)
                .filter(movement -> accountJpaRepository
                        .findByIdAndCustomerId(movement.getAccountId(), customerId).isPresent())
                .map(MovementPersistenceMapper::toDomain);
    }

    @Override
    public List<Movement> findAllByAccount(Long customerId, Long accountId) {
        if (accountJpaRepository.findByIdAndCustomerId(accountId, customerId).isEmpty()) {
            return List.of();
        }
        return movementJpaRepository.findAllByAccountId(accountId).stream()
                .map(MovementPersistenceMapper::toDomain)
                .toList();
    }
}
