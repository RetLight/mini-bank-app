package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import com.project.minibank.movement.domain.model.Transfer;

final class TransferPersistenceMapper {

    private TransferPersistenceMapper() {
    }

    static Transfer toDomain(TransferEntity entity) {
        return new Transfer(
                entity.getId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getRejectionReason(),
                entity.getRequestedAt(),
                entity.getProcessedAt(),
                entity.getType(),
                entity.getDestinationCci(),
                entity.getDestinationBank(),
                entity.getDestinationHolder(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId()
        );
    }

    static TransferEntity toEntity(Transfer transfer) {
        return new TransferEntity(
                transfer.getId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getStatus(),
                transfer.getRejectionReason(),
                transfer.getRequestedAt(),
                transfer.getProcessedAt(),
                transfer.getType(),
                transfer.getDestinationCci(),
                transfer.getDestinationBank(),
                transfer.getDestinationHolder(),
                transfer.getSourceAccountId(),
                transfer.getDestinationAccountId()
        );
    }
}
