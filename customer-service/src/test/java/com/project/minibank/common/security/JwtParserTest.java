package com.project.minibank.common.security;

import com.project.minibank.TestTokens;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// El JwtParser de este servicio frente a los tokens que firma auth-service (forma copiada en TestTokens).
class JwtParserTest {

    private final JwtParser parser = new JwtParser(TestTokens.SECRET);

    @Test
    void aCustomerTokenYieldsThePrincipalFromItsClaimsOnly() {
        assertThat(parser.parse(TestTokens.forCustomer(1))).contains(new JwtPrincipal("customer-1", 1L, "CUSTOMER"));
    }

    @Test
    void theAdminRoleIsRecognised() {
        assertThat(parser.parse(TestTokens.forAdmin(2))).get().extracting(JwtPrincipal::isAdmin).isEqualTo(true);
    }

    @Test
    void theServiceTokenIsValidWithoutACustomer() {
        assertThat(parser.parse(TestTokens.forService())).contains(new JwtPrincipal("auth-service", null, "SERVICE"));
    }

    @Test
    void aUserTokenWithoutCustomerIdIsRejected() {
        assertThat(parser.parse(TestTokens.customerWithoutCustomerId())).isEmpty();
    }

    @Test
    void aTamperedTokenIsRejected() {
        assertThat(parser.parse(TestTokens.forCustomer(1) + "x")).isEmpty();
    }

    @Test
    void aTokenSignedWithAnotherSecretIsRejected() {
        assertThat(parser.parse(TestTokens.signedWithAnotherSecret(1))).isEmpty();
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
