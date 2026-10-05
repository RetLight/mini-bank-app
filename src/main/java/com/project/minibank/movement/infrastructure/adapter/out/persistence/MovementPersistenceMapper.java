package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import com.project.minibank.movement.domain.model.Movement;

final class MovementPersistenceMapper {

    private MovementPersistenceMapper() {
    }

    static Movement toDomain(MovementEntity entity) {
        return new Movement(
                entity.getId(),
                entity.getType(),
                entity.getAmount(),
                entity.getResultingBalance(),
                entity.getOccurredAt(),
                entity.getAccountId(),
                entity.getTransferId()
        );
    }

    static MovementEntity toEntity(Movement movement) {
        return new MovementEntity(
                movement.getId(),
                movement.getType(),
                movement.getAmount(),
                movement.getResultingBalance(),
                movement.getOccurredAt(),
                movement.getAccountId(),
                movement.getTransferId()
        );
    }
}
