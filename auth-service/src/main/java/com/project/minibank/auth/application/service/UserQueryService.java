package com.project.minibank.auth.application.service;

import com.project.minibank.auth.application.port.in.GetMyUserUseCase;
import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import com.project.minibank.auth.domain.exception.UserNotFoundException;
import com.project.minibank.auth.domain.model.User;
import org.springframework.stereotype.Service;

// El token prueba quién era el usuario cuando se emitió; esto dice cómo está ahora (existe, bloqueado o no).
@Service
public class UserQueryService implements GetMyUserUseCase {

    private final UserRepositoryPort userRepository;

    public UserQueryService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User me(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
    }
}
