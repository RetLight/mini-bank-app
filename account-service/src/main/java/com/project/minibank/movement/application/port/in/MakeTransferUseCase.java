package com.project.minibank.movement.application.port.in;

import com.project.minibank.movement.domain.model.Transfer;

public interface MakeTransferUseCase {
    Transfer makeTransfer(MakeTransferCommand cmd);
}
