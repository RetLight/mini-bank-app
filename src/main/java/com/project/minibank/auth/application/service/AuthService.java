package com.project.minibank.auth.application.service;

import com.project.minibank.auth.application.port.in.LoginCommand;
import com.project.minibank.auth.application.port.in.LoginUseCase;
import com.project.minibank.auth.application.port.out.CredentialsVerifierPort;
import com.project.minibank.auth.application.port.out.TokenGeneratorPort;
import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import com.project.minibank.auth.domain.exception.InvalidCredentialsException;
import com.project.minibank.auth.domain.model.AccessToken;
import com.project.minibank.auth.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase {

    private final CredentialsVerifierPort credentialsVerifier;
    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort tokenGenerator;

    public AuthService(CredentialsVerifierPort credentialsVerifier, UserRepositoryPort userRepository,
                       TokenGeneratorPort tokenGenerator) {
        this.credentialsVerifier = credentialsVerifier;
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public AccessToken login(LoginCommand command) {
        credentialsVerifier.verify(command.username(), command.password());

        User user = userRepository.findByUsername(command.username())
                .orElseThrow(InvalidCredentialsException::new);

        return tokenGenerator.generate(user);
    }
}
