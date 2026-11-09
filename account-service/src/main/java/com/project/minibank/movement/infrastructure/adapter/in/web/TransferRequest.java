package com.project.minibank.movement.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class TransferRequest {

    @NotNull(message = "La cuenta origen es obligatoria")
    private Long sourceAccountId;

    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @NotBlank(message = "El CCI destino es obligatorio")
    @Pattern(regexp = "\\d{1,20}", message = "El CCI destino solo debe contener dígitos (máximo 20)")
    private String destinationCci;

    @NotBlank(message = "El banco destino es obligatorio")
    @Size(max = 50, message = "El banco destino no puede superar 50 caracteres")
    private String destinationBank;

    @NotBlank(message = "El titular destino es obligatorio")
    @Size(max = 100, message = "El titular destino no puede superar 100 caracteres")
    private String destinationHolder;

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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
}
