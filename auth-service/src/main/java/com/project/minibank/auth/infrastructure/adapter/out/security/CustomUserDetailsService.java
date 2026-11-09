package com.project.minibank.auth.infrastructure.adapter.out.security;

import com.project.minibank.auth.application.port.out.UserRepositoryPort;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Adapta el User del dominio a lo que Spring Security necesita para comprobar la contraseña en el login.
// Vive en el área auth y no en common.security: common no conoce ninguna área de negocio (ArchitectureTest).
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort users;

    public CustomUserDetailsService(UserRepositoryPort users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return users.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .roles(user.getRole())
                        .accountLocked(user.isBlocked())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
