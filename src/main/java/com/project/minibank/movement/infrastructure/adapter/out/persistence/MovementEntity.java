package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movement")
public class MovementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    private BigDecimal amount;

    @Column(name = "resulting_balance")
    private BigDecimal resultingBalance;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "transfer_id")
    private Long transferId;

    public MovementEntity() {
    }

    public MovementEntity(Long id, String type, BigDecimal amount, BigDecimal resultingBalance,
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
