package com.project.minibank.movement.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransferResponse {

    private Long id;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String rejectionReason;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String type;
    private String destinationCci;
    private String destinationBank;
    private String destinationHolder;
    private Long sourceAccountId;
    private Long destinationAccountId;

    public TransferResponse() {
    }

    public TransferResponse(Long id, BigDecimal amount, String currency, String status, String rejectionReason,
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

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDestinationCci() {
        return destinationCci;
    }

    public void setDestinationCci(String destinationCci) {
        this.destinationCci = destinationCci;
    }

    public String getDestinationBank() {
        return destinationBank;
    }

    public void setDestinationBank(String destinationBank) {
        this.destinationBank = destinationBank;
    }

    public String getDestinationHolder() {
        return destinationHolder;
    }

    public void setDestinationHolder(String destinationHolder) {
        this.destinationHolder = destinationHolder;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }
}
