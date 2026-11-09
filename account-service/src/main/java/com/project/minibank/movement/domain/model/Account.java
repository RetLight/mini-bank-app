package com.project.minibank.movement.domain.model;

import com.project.minibank.movement.domain.exception.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Account {
    private final Long id;
    private final String accountNumber;
    private final String cci;
    private final String type;
    private final String currency;
    private BigDecimal balance;
    private final String status;
    private final LocalDateTime openedAt;
    private final Long customerId;

    public Account(Long id, String accountNumber, String cci, String type, String currency, BigDecimal balance, String status, LocalDateTime openedAt, Long customerId) {
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

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCci() {
        return cci;
    }

    public String getType() {
        return type;
    }

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

    public void increase(BigDecimal amount){
        if (!this.status.equals("ACTIVE")){
            throw new InactiveAccountException(this.id, this.status);
        }
        balance = this.balance.add(amount);
    }

    public void decrease(BigDecimal amount){
        if (!this.status.equals("ACTIVE")){
            throw new InactiveAccountException(this.id, this.status);
        }
        if (balance.compareTo(amount) < 0 ){
            throw new InsufficientAccountBalanceException(this.id);
        }
        balance = balance.subtract(amount);
    }

    public boolean belongsTo(Long customerId){
        return this.customerId.equals(customerId);
    }

}
