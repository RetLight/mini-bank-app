package com.project.minibank.movement.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transfer {
    private final Long id;
    private final BigDecimal amount;
    private final String currency;
    private final String status;
    private final String rejectionReason;
    private final LocalDateTime requestedAt;
    private final LocalDateTime processedAt;
    private final String type;
    private final String destinationCci;
    private final String destinationBank;
    private final String destinationHolder;
    private final Long sourceAccountId;
    private final Long destinationAccountId;

    public Transfer(Long id, BigDecimal amount, String currency, String status, String rejectionReason, LocalDateTime requestedAt, LocalDateTime processedAt, String type, String destinationCci, String destinationBank, String destinationHolder, Long sourceAccountId, Long destinationAccountId) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("La moneda es obligatoria");
        }
        if (destinationCci == null || !destinationCci.matches("\\d+")) {
            throw new IllegalArgumentException("El CCI destino solo debe contener dígitos");
        }
        if (destinationBank == null || destinationBank.isBlank()) {
            throw new IllegalArgumentException("El banco destino es obligatorio");
        }
        if (destinationHolder == null || destinationHolder.isBlank()) {
            throw new IllegalArgumentException("El titular destino es obligatorio");
        }
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.rejectionReason = rejectionReason;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.type = type;
        this.destinationCci = destinationCci;
        this.destinationBank = destinationBank;
        this.destinationHolder = destinationHolder;
        this.sourceAccountId = Objects.requireNonNull(sourceAccountId, "La cuenta origen es obligatoria");
        this.destinationAccountId = destinationAccountId;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public String getType() {
        return type;
    }

    public String getDestinationCci() {
        return destinationCci;
    }

    public String getDestinationBank() {
        return destinationBank;
    }

    public String getDestinationHolder() {
        return destinationHolder;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }
}
