package com.project.minibank.movement.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number")
    private String accountNumber;

    private String cci;

    private String type;

    private String currency;

    private BigDecimal balance;

    private String status;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "customer_id")
    private Long customerId;

    public AccountEntity() {
    }

    public AccountEntity(Long id, String accountNumber, String cci, String type, String currency,
                         BigDecimal balance, String status, LocalDateTime openedAt, Long customerId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.cci = cci;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.openedAt = openedAt;
        this.customerId = customerId;
    }

    public Long getId() { return id; }

    public String getAccountNumber() { return accountNumber;}

    public String getCci() {
        return cci;
    }

    public String getType() { return type; }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getOpenedAt() {
        return openedAt;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
