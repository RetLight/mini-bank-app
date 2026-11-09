package com.project.minibank.customer.infrastructure.adapter.in.web;

public class FavoriteResponse {

    private Long id;
    private String alias;
    private String accountNumber;
    private String bank;
    private String holder;
    private boolean ownerVerified;

    public FavoriteResponse() {
    }

    public FavoriteResponse(Long id, String alias, String accountNumber, String bank, String holder,
                            boolean ownerVerified) {
        this.id = id;
        this.alias = alias;
        this.accountNumber = accountNumber;
        this.bank = bank;
        this.holder = holder;
        this.ownerVerified = ownerVerified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public boolean isOwnerVerified() {
        return ownerVerified;
    }

    public void setOwnerVerified(boolean ownerVerified) {
        this.ownerVerified = ownerVerified;
    }
}
