package com.project.minibank.movement.application.port.in;

import java.math.BigDecimal;

public record MakeTransferCommand(
        Long customerId,
        BigDecimal amount,
        String destinationCci,
        String destinationBank,
        String destinationHolder,
        Long sourceAccountId
){

}
