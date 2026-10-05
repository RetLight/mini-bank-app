package com.project.minibank.auth.infrastructure.security;

import com.project.minibank.auth.infrastructure.adapter.out.persistence.UserJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TokenService {

    private final JwtEncoder encoder;
    private final UserJpaRepository userJpaRepository;

    public TokenService(JwtEncoder encoder, UserJpaRepository userJpaRepository) {
        this.encoder = encoder;
        this.userJpaRepository = userJpaRepository;
    }

    public String generate(Authentication auth) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("minibank-api")
                .subject(auth.getName())
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .claim("roles", roles(auth))
                .claim("customerId", userJpaRepository.findByUsername(auth.getName()).orElseThrow().getCustomerId())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private List<String> roles(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(role -> role.startsWith("ROLE_"))
                .map(role -> role.replace("ROLE_", ""))
                .toList();
    }
}
