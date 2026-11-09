package com.project.minibank.customer.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class FavoriteRequest {

    @NotBlank(message = "El alias es obligatorio")
    @Size(max = 50, message = "El alias no puede superar 50 caracteres")
    private String alias;

    @NotBlank(message = "El número de cuenta es obligatorio")
    @Pattern(regexp = "\\d{1,20}", message = "El número de cuenta solo debe contener dígitos (máximo 20)")
    private String accountNumber;

    @NotBlank(message = "El banco es obligatorio")
    @Size(max = 50, message = "El banco no puede superar 50 caracteres")
    private String bank;

    @NotBlank(message = "El titular es obligatorio")
    @Size(max = 100, message = "El titular no puede superar 100 caracteres")
    private String holder;

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
}
