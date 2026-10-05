package com.project.minibank.movement.infrastructure.adapter.in.web;

import com.project.minibank.movement.domain.model.Movement;

import java.util.List;

final class MovementWebMapper {

    private MovementWebMapper() {
    }

    static MovementResponse toResponse(Movement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getType(),
                movement.getAmount(),
                movement.getResultingBalance(),
                movement.getOccurredAt(),
                movement.getAccountId(),
                movement.getTransferId()
        );
    }

    static List<MovementResponse> toResponse(List<Movement> movements) {
        return movements.stream()
                .map(MovementWebMapper::toResponse)
                .toList();
    }
}
