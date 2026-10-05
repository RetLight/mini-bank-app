package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer")
public class TransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    private String currency;

    private String status;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    private String type;

    @Column(name = "destination_cci")
    private String destinationCci;

    @Column(name = "destination_bank")
    private String destinationBank;

    @Column(name = "destination_holder")
    private String destinationHolder;

    @Column(name = "source_account_id")
    private Long sourceAccountId;

    @Column(name = "destination_account_id")
    private Long destinationAccountId;

    public TransferEntity() {
    }

    public TransferEntity(Long id, BigDecimal amount, String currency, String status, String rejectionReason,
                          LocalDateTime requestedAt, LocalDateTime processedAt, String type, String destinationCci,
                          String destinationBank, String destinationHolder, Long sourceAccountId,
                          Long destinationAccountId) {
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
        this.sourceAccountId = sourceAccountId;
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
