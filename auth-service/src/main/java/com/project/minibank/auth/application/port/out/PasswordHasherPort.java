package com.project.minibank.auth.application.port.out;

public interface PasswordHasherPort {
    String hash(String rawPassword);
}
