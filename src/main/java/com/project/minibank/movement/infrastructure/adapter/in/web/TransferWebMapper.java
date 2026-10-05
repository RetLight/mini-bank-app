package com.project.minibank.movement.infrastructure.adapter.in.web;

import com.project.minibank.movement.application.port.in.MakeTransferCommand;
import com.project.minibank.movement.domain.model.Transfer;

import java.util.List;

final class TransferWebMapper {

    private TransferWebMapper() {
    }

    static MakeTransferCommand toCommand(Long customerId, TransferRequest request) {
        return new MakeTransferCommand(
                customerId,
                request.getAmount(),
                request.getDestinationCci(),
                request.getDestinationBank(),
                request.getDestinationHolder(),
                request.getSourceAccountId()
        );
    }

    static TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
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

    static List<TransferResponse> toResponse(List<Transfer> transfers) {
        return transfers.stream()
                .map(TransferWebMapper::toResponse)
                .toList();
    }
}
