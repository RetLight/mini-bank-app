package com.project.minibank.auth.infrastructure.adapter.in.web;

import com.project.minibank.auth.application.port.in.GetMyUserUseCase;
import com.project.minibank.auth.domain.model.User;
import com.project.minibank.common.security.JwtPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Estado actual del usuario del token. Lo consulta customer-service (Feign) al crear un favorito, reenviando el
 * token del propio usuario: cada uno solo puede preguntar por sí mismo. Nunca devuelve la contraseña.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    public record UserStatusResponse(String username, Long customerId, String role, boolean blocked) {
    }

    private final GetMyUserUseCase getMyUserUseCase;

    public UserController(GetMyUserUseCase getMyUserUseCase) {
        this.getMyUserUseCase = getMyUserUseCase;
    }

    @GetMapping("/me")
    public UserStatusResponse me(@AuthenticationPrincipal JwtPrincipal principal) {
        User user = getMyUserUseCase.me(principal.username());
        return new UserStatusResponse(user.getUsername(), user.getCustomerId(), user.getRole(), user.isBlocked());
    }
}
