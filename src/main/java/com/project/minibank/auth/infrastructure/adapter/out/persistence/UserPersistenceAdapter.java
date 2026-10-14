package com.project.minibank.auth.infrastructure.adapter.out.persistence;

import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import com.project.minibank.auth.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    public UserPersistenceAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(UserPersistenceMapper::toDomain);
    }
}
