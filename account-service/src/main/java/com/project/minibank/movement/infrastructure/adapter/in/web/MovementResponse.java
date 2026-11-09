package com.project.minibank.movement.infrastructure.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovementResponse {

    private Long id;
    private String type;
    private BigDecimal amount;
    private BigDecimal resultingBalance;
    private LocalDateTime occurredAt;
    private Long accountId;
    private Long transferId;

    public MovementResponse() {
    }

    public MovementResponse(Long id, String type, BigDecimal amount, BigDecimal resultingBalance,
                            LocalDateTime occurredAt, Long accountId, Long transferId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.resultingBalance = resultingBalance;
        this.occurredAt = occurredAt;
        this.accountId = accountId;
        this.transferId = transferId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }

    public void setResultingBalance(BigDecimal resultingBalance) {
        this.resultingBalance = resultingBalance;
    }

    public LocalDateTime getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }
}
