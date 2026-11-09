package com.project.minibank.auth.application.service;

import com.project.minibank.auth.application.port.in.LoginCommand;
import com.project.minibank.auth.application.port.in.LoginUseCase;
import com.project.minibank.auth.application.port.in.RegisterCommand;
import com.project.minibank.auth.application.port.in.RegisterUseCase;
import com.project.minibank.auth.application.port.out.CustomerProvisioningPort;
import com.project.minibank.auth.application.port.out.NewCustomer;
import com.project.minibank.auth.application.port.out.PasswordHasherPort;
import com.project.minibank.auth.application.port.out.CredentialsVerifierPort;
import com.project.minibank.auth.application.port.out.TokenGeneratorPort;
import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import com.project.minibank.auth.domain.exception.InvalidCredentialsException;
import com.project.minibank.auth.domain.exception.UsernameAlreadyUsedException;
import com.project.minibank.auth.domain.model.AccessToken;
import com.project.minibank.auth.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase, RegisterUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final CredentialsVerifierPort credentialsVerifier;
    private final UserRepositoryPort userRepository;
    private final TokenGeneratorPort tokenGenerator;
    private final CustomerProvisioningPort customerProvisioning;
    private final PasswordHasherPort passwordHasher;

    public AuthService(CredentialsVerifierPort credentialsVerifier, UserRepositoryPort userRepository,
                       TokenGeneratorPort tokenGenerator, CustomerProvisioningPort customerProvisioning,
                       PasswordHasherPort passwordHasher) {
        this.credentialsVerifier = credentialsVerifier;
        this.userRepository = userRepository;
        this.tokenGenerator = tokenGenerator;
        this.customerProvisioning = customerProvisioning;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public AccessToken login(LoginCommand command) {
        credentialsVerifier.verify(command.username(), command.password());

        User user = userRepository.findByUsername(command.username())
                .orElseThrow(InvalidCredentialsException::new);

        return tokenGenerator.generate(user);
    }

    // Registro en dos servicios: primero el cliente (customer-service), después el usuario que lo usa para entrar.
    // El usuario repetido se rechaza antes de llamar a nadie. Si customer-service rechaza el cliente o no responde,
    // no se crea ningún usuario. Devuelve un token: el usuario queda dentro sin hacer login aparte.
    @Override
    public AccessToken register(RegisterCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new UsernameAlreadyUsedException(command.username());
        }
        Long customerId = customerProvisioning.create(new NewCustomer(command.documentType(), command.documentNumber(),
                command.firstNames(), command.lastNames(), command.email(), command.phone()));
        User saved = userRepository.save(User.register(command.username(), passwordHasher.hash(command.password()), customerId));
        log.info("Usuario {} registrado con el cliente {}", saved.getUsername(), customerId);
        return tokenGenerator.generate(saved);
    }
}
