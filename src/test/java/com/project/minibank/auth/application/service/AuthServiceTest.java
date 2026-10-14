package com.project.minibank.auth.application.service;

import com.project.minibank.auth.application.port.in.LoginCommand;
import com.project.minibank.auth.application.port.out.CredentialsVerifierPort;
import com.project.minibank.auth.application.port.out.TokenGeneratorPort;
import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import com.project.minibank.auth.domain.exception.InvalidCredentialsException;
import com.project.minibank.auth.domain.exception.UserBlockedException;
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
}
