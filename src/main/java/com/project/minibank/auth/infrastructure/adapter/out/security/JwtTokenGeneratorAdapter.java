package com.project.minibank.auth.infrastructure.adapter.out.security;

import com.project.minibank.auth.application.port.out.TokenGeneratorPort;
import com.project.minibank.auth.domain.model.AccessToken;
import com.project.minibank.auth.domain.model.User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class JwtTokenGeneratorAdapter implements TokenGeneratorPort {

    private static final Duration EXPIRATION = Duration.ofMinutes(30);

    private final JwtEncoder encoder;

    public JwtTokenGeneratorAdapter(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public AccessToken generate(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("minibank-api")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plus(EXPIRATION))
                .claim("roles", List.of(user.getRole()))
                .claim("customerId", user.getCustomerId())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new AccessToken(token, "Bearer", EXPIRATION.toSeconds());
    }
}
