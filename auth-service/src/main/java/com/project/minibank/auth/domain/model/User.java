package com.project.minibank.auth.domain.model;

import java.util.Objects;

public class User {

    private final Long id;
    private final String username;
    private final String passwordHash;
    private final String role;
    private final boolean blocked;
    private final Long customerId;

    // Usuario nuevo de un registro: siempre CUSTOMER y sin bloquear.
    public static User register(String username, String passwordHash, Long customerId) {
        return new User(null, username, passwordHash, "CUSTOMER", false, customerId);
    }

    public User(Long id, String username, String passwordHash, String role, boolean blocked, Long customerId) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("El usuario es obligatorio");
        }
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio");
        }
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.blocked = blocked;
        this.customerId = Objects.requireNonNull(customerId, "El cliente es obligatorio");
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getRole() {
        return role;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public Long getCustomerId() {
        return customerId;
    }
}
