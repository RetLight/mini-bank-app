package com.project.minibank.auth.infrastructure.adapter.out.security;

import com.project.minibank.auth.application.port.out.TokenGeneratorPort;
import com.project.minibank.auth.domain.model.AccessToken;
import com.project.minibank.auth.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

// Solo auth-service firma tokens. Los demás solo los validan (common.security.JwtParser).
// Los claims son el contrato entre servicios: si cambian, customer-service y account-service dejan de entenderlos.
@Component
public class JwtTokenIssuer implements TokenGeneratorPort {

    private final SecretKey key;
    private final Duration expiration;

    public JwtTokenIssuer(@Value("${app.jwt.secret}") String secret,
                          @Value("${app.jwt.expiration-minutes:30}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofMinutes(expirationMinutes);
    }

    @Override
    public AccessToken generate(User user) {
        Instant now = Instant.now();
        String token = Jwts.builder()
                .issuer("minibank-api")
                .subject(user.getUsername())
                .claim("roles", List.of(user.getRole()))
                .claim("customerId", user.getCustomerId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
        return new AccessToken(token, "Bearer", expiration.toSeconds());
    }
}
