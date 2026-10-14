package com.project.minibank.auth.infrastructure.adapter.out.security;

import com.project.minibank.auth.application.port.out.CredentialsVerifierPort;
import com.project.minibank.auth.domain.exception.InvalidCredentialsException;
import com.project.minibank.auth.domain.exception.UserBlockedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class SpringCredentialsVerifierAdapter implements CredentialsVerifierPort {

    private final AuthenticationManager authenticationManager;

    public SpringCredentialsVerifierAdapter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void verify(String username, String password) {
        try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(username, password));
        } catch (LockedException ex) {
            throw new UserBlockedException();
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }
    }
}
