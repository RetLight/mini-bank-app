package com.project.minibank.customer.infrastructure.adapter.out.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

// Cliente HTTP declarativo, en infraestructura y detrás de UserStatusPort: el dominio nunca oye hablar de Feign.
// Llama directo a auth-service (URL de config-repo), sin pasar por el gateway: es tráfico interno.
@FeignClient(name = "auth-service", url = "${minibank.services.auth-url}", configuration = AuthClientConfig.class)
interface AuthClient {

    // Copia propia del contrato de GET /users/me, que es de auth-service: no se importa nada del otro proyecto.
    record UserStatusResponse(String username, Long customerId, String role, boolean blocked) {
    }

    @GetMapping("/users/me")
    UserStatusResponse me();
}
