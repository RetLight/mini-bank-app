package com.project.minibank.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

// Solo validación: comprueba firma y caducidad. Firmar tokens es trabajo de auth-service.
// Keys.hmacShaKeyFor exige al menos 32 bytes: con un JWT_SECRET más corto el servicio no arranca (falla rápido).
@Component
public class JwtParser {

    private final SecretKey key;

    public JwtParser(@Value("${app.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Vacío si el token está mal formado, manipulado, caducado o le faltan los claims esperados. */
    public Optional<JwtPrincipal> parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            List<?> roles = claims.get("roles", List.class);
            Long customerId = claims.get("customerId", Long.class);
            if (claims.getSubject() == null || roles == null || roles.size() != 1 || !(roles.getFirst() instanceof String role)) {
                return Optional.empty();
            }
            // Solo el token de servicio puede no llevar cliente: el de un usuario siempre lo lleva.
            if (customerId == null && !JwtPrincipal.SERVICE.equals(role)) {
                return Optional.empty();
            }
            return Optional.of(new JwtPrincipal(claims.getSubject(), customerId, role));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
