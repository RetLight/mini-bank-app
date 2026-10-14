package com.project.minibank.auth.application.port.out;

import com.project.minibank.auth.domain.model.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findByUsername(String username);
}
