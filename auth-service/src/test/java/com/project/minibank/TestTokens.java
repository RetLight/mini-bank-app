package com.project.minibank;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

// Tokens con la misma forma que los que firma auth-service (JwtTokenIssuer): emisor minibank-api, claim roles y
// claim customerId. Es el contrato entre los servicios; si auth-service lo cambia, este helper también.
public final class TestTokens {

    public static final String SECRET = "test-secret-for-jwt-tests-must-be-at-least-32-bytes-long";

    private TestTokens() {
    }

    public static String forCustomer(long customerId) {
        return sign(SECRET, "customer-" + customerId, customerId, "CUSTOMER", Instant.now().plus(Duration.ofMinutes(30)));
    }

    public static String forAdmin(long customerId) {
        return sign(SECRET, "customer-" + customerId, customerId, "ADMIN", Instant.now().plus(Duration.ofMinutes(30)));
    }

    // El token con el que auth-service se identifica en el registro: rol SERVICE y sin customerId.
    public static String forService() {
        return sign(SECRET, "auth-service", null, "SERVICE", Instant.now().plus(Duration.ofSeconds(60)));
    }

    public static String signedWithAnotherSecret(long customerId) {
        return sign("another-secret-for-jwt-tests-at-least-32-bytes", "customer-" + customerId, customerId, "CUSTOMER",
                Instant.now().plus(Duration.ofMinutes(30)));
    }

    public static String expired(long customerId) {
        return sign(SECRET, "customer-" + customerId, customerId, "CUSTOMER", Instant.now().minus(Duration.ofHours(2)));
    }

    // Un token de usuario sin customerId: el JwtParser debe rechazarlo.
    public static String customerWithoutCustomerId() {
        return sign(SECRET, "demo", null, "CUSTOMER", Instant.now().plus(Duration.ofMinutes(30)));
    }

    private static String sign(String secret, String subject, Long customerId, String role, Instant expiresAt) {
        var builder = Jwts.builder()
                .issuer("minibank-api")
                .subject(subject)
                .claim("roles", List.of(role))
                .issuedAt(Date.from(expiresAt.minus(Duration.ofMinutes(30))))
                .expiration(Date.from(expiresAt));
        if (customerId != null) {
            builder.claim("customerId", customerId);
        }
        return builder.signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256).compact();
    }
}
