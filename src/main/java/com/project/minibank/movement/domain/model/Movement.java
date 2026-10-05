package com.project.minibank.movement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Movement {
    private final Long id;
    private final String type;
    private final BigDecimal amount;
    private final BigDecimal resultingBalance;
    private final LocalDateTime occurredAt;
    private final Long accountId;
    private final Long transferId;

    public Movement(Long id, String type, BigDecimal amount, BigDecimal resultingBalance, LocalDateTime occurredAt, Long accountId, Long transferId) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        if (resultingBalance == null) {
            throw new IllegalArgumentException("El saldo resultante es obligatorio");
        }
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.resultingBalance = resultingBalance;
        this.occurredAt = occurredAt;
        this.accountId = Objects.requireNonNull(accountId, "La cuenta es obligatoria");
        this.transferId = transferId;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getTransferId() {
        return transferId;
    }
}
