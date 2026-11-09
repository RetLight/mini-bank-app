package com.project.minibank.customer.domain.model;

import java.util.Objects;

public class Favorite {

    private final Long id;
    private final String alias;
    private final String accountNumber;
    private final String bank;
    private final String holder;
    private final Long customerId;
    // false = se guardó mientras auth-service no podía confirmar que el usuario seguía activo.
    private final boolean ownerVerified;

    public Favorite(Long id, String alias, String accountNumber, String bank, String holder, Long customerId) {
        this(id, alias, accountNumber, bank, holder, customerId, true);
    }

    public Favorite(Long id, String alias, String accountNumber, String bank, String holder, Long customerId,
                    boolean ownerVerified) {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("El alias es obligatorio");
        }
        if (accountNumber == null || !accountNumber.matches("\\d+")) {
            throw new IllegalArgumentException("El número de cuenta solo debe contener dígitos");
        }
        if (bank == null || bank.isBlank()) {
            throw new IllegalArgumentException("El banco es obligatorio");
        }
        if (holder == null || holder.isBlank()) {
            throw new IllegalArgumentException("El titular es obligatorio");
        }
        this.id = id;
        this.alias = alias;
        this.accountNumber = accountNumber;
        this.bank = bank;
        this.holder = holder;
        this.customerId = Objects.requireNonNull(customerId, "El cliente es obligatorio");
        this.ownerVerified = ownerVerified;
    }

    public static Favorite create(String alias, String accountNumber, String bank, String holder, Long customerId) {
        return new Favorite(null, alias, accountNumber, bank, holder, customerId);
    }

    // Mismo favorito (mismo id y cliente) con otros datos; las validaciones del constructor se vuelven a aplicar.
    public Favorite withData(String alias, String accountNumber, String bank, String holder) {
        return new Favorite(id, alias, accountNumber, bank, holder, customerId, ownerVerified);
    }

    // El mismo favorito, marcado como guardado sin poder verificar al usuario.
    public Favorite unverified() {
        return new Favorite(id, alias, accountNumber, bank, holder, customerId, false);
    }

    public boolean isOwnerVerified() {
        return ownerVerified;
    }

    public boolean isSameAccountAs(Favorite other) {
        return accountNumber.equals(other.accountNumber) && bank.equalsIgnoreCase(other.bank);
    }

    public boolean hasSameAliasAs(Favorite other) {
        return alias.equalsIgnoreCase(other.alias);
    }

    public Long getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBank() {
        return bank;
    }

    public String getHolder() {
        return holder;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
