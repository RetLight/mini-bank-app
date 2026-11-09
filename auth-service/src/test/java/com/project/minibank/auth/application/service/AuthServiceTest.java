package com.project.minibank.auth.application.service;

import com.project.minibank.auth.application.port.in.LoginCommand;
import com.project.minibank.auth.application.port.in.RegisterCommand;
import com.project.minibank.auth.application.port.out.CustomerProvisioningPort;
import com.project.minibank.auth.application.port.out.NewCustomer;
import com.project.minibank.auth.application.port.out.PasswordHasherPort;
import com.project.minibank.auth.application.port.out.CredentialsVerifierPort;
import com.project.minibank.auth.application.port.out.TokenGeneratorPort;
import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import com.project.minibank.auth.domain.exception.InvalidCredentialsException;
import com.project.minibank.auth.domain.exception.UserBlockedException;
import com.project.minibank.auth.domain.exception.UsernameAlreadyUsedException;
import com.project.minibank.auth.domain.model.AccessToken;
import com.project.minibank.auth.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    CredentialsVerifierPort credentialsVerifierPort;

    @Mock
    UserRepositoryPort userRepositoryPort;

    @Mock
    TokenGeneratorPort tokenGeneratorPort;

    @Mock
    CustomerProvisioningPort customerProvisioningPort;

    @Mock
    PasswordHasherPort passwordHasherPort;

    @InjectMocks
    AuthService authService;

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        User user = new User(1L, "demo", "hash", "CUSTOMER", false, 10L);
        AccessToken token = new AccessToken("jwt", "Bearer", 1800);
        when(userRepositoryPort.findByUsername("demo"))
                .thenReturn(Optional.of(user));
        when(tokenGeneratorPort.generate(user))
                .thenReturn(token);

        AccessToken valueReturned = authService.login(new LoginCommand("demo", "demo123"));

        assertEquals("jwt", valueReturned.value());
        verify(credentialsVerifierPort).verify("demo", "demo123");
    }

    @Test
    void shouldThrowWhenCredentialsAreInvalid() {
        doThrow(new InvalidCredentialsException())
                .when(credentialsVerifierPort).verify("demo", "wrong");

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(new LoginCommand("demo", "wrong")));
        verify(tokenGeneratorPort, never()).generate(any(User.class));
    }

    @Test
    void shouldThrowWhenUserIsBlocked() {
        doThrow(new UserBlockedException())
                .when(credentialsVerifierPort).verify("pedro", "pedro123");

        assertThrows(UserBlockedException.class,
                () -> authService.login(new LoginCommand("pedro", "pedro123")));
        verify(tokenGeneratorPort, never()).generate(any(User.class));
    }

    @Test
    void shouldCreateTheCustomerFirstAndThenTheUser() {
        when(userRepositoryPort.existsByUsername("eva")).thenReturn(false);
        when(customerProvisioningPort.create(any(NewCustomer.class))).thenReturn(5L);
        when(passwordHasherPort.hash("Secret123")).thenReturn("hash");
        when(userRepositoryPort.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenGeneratorPort.generate(any(User.class))).thenReturn(new AccessToken("jwt", "Bearer", 1800));

        AccessToken token = authService.register(registerCommand("eva"));

        assertEquals("jwt", token.value());
        var order = inOrder(customerProvisioningPort, userRepositoryPort);
        order.verify(customerProvisioningPort).create(any(NewCustomer.class));
        order.verify(userRepositoryPort).save(argThat(u -> u.getCustomerId().equals(5L) && "CUSTOMER".equals(u.getRole())));
    }

    @Test
    void shouldNotCallCustomerServiceWhenTheUsernameIsTaken() {
        when(userRepositoryPort.existsByUsername("demo")).thenReturn(true);

        assertThrows(UsernameAlreadyUsedException.class, () -> authService.register(registerCommand("demo")));
        verify(customerProvisioningPort, never()).create(any(NewCustomer.class));
        verify(userRepositoryPort, never()).save(any(User.class));
    }

    private static RegisterCommand registerCommand(String username) {
        return new RegisterCommand(username, "Secret123", "DNI", "10000099", "Eva", "Rojas", "eva@mail.com", "999000111");
    }
}
