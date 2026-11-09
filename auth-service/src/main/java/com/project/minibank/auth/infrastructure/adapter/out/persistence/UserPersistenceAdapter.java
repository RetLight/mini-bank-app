package com.project.minibank.auth.infrastructure.adapter.out.persistence;

import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import com.project.minibank.auth.domain.model.User;
import org.springframework.stereotype.Component;

import com.project.minibank.auth.domain.exception.UsernameAlreadyUsedException;
import org.springframework.dao.DataIntegrityViolationException;

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

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }

    @Override
    public User save(User user) {
        try {
            return UserPersistenceMapper.toDomain(userJpaRepository.saveAndFlush(
                    new UserEntity(user.getUsername(), user.getPasswordHash(), user.getRole(), user.getCustomerId())));
        } catch (DataIntegrityViolationException e) {
            // UNIQUE(username): dos registros simultáneos con el mismo usuario pueden pasar existsByUsername.
            throw new UsernameAlreadyUsedException(user.getUsername());
        }
    }
}
