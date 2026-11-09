package com.project.minibank.common.security;

import com.project.minibank.TestTokens;
import com.project.minibank.auth.domain.model.User;
import com.project.minibank.auth.infrastructure.adapter.out.security.JwtTokenIssuer;
import com.project.minibank.auth.infrastructure.adapter.out.security.ServiceTokenIssuer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// Emisor y validador están separados a propósito: un servicio que solo valida tokens nunca necesita el emisor.
class JwtParserTest {

    private final JwtParser parser = new JwtParser(TestTokens.SECRET);
    private final JwtTokenIssuer issuer = new JwtTokenIssuer(TestTokens.SECRET, 30);

    @Test
    void whatTheIssuerSignsTheParserReadsBack() {
        String token = issuer.generate(new User(1L, "demo", "hash", "CUSTOMER", false, 1L)).value();

        assertThat(parser.parse(token)).contains(new JwtPrincipal("demo", 1L, "CUSTOMER"));
    }

    @Test
    void theServiceTokenHasRoleServiceAndNoCustomer() {
        assertThat(parser.parse(new ServiceTokenIssuer(TestTokens.SECRET).issue()))
                .contains(new JwtPrincipal("auth-service", null, "SERVICE"));
    }

    @Test
    void aTokenSignedWithAnotherSecretIsRejected() {
        String token = new JwtTokenIssuer("another-secret-for-jwt-tests-at-least-32-bytes", 30)
                .generate(new User(1L, "demo", "hash", "CUSTOMER", false, 1L)).value();

        assertThat(parser.parse(token)).isEmpty();
    }

    @Test
    void anExpiredTokenIsRejected() {
        assertThat(parser.parse(TestTokens.expired(1))).isEmpty();
    }

    @Test
    void garbageIsRejected() {
        assertThat(parser.parse("no-es-un-jwt")).isEmpty();
    }
}
