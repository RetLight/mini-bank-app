package com.project.minibank;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

// Lee los claims de un token firmado con el secreto de los tests (verifica la firma).
final class TestJwt {

    private TestJwt() {
    }

    static Claims claims(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(TestTokens.SECRET.getBytes(StandardCharsets.UTF_8)))
                .build().parseSignedClaims(token).getPayload();
    }
}
