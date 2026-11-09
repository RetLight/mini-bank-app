package com.project.minibank;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Al crear un favorito, customer-service pregunta a auth-service (Feign) si el usuario sigue activo. Aquí auth-service
// es un servidor falso: activo, bloqueado, inexistente, fallando, caído, lento y con el circuito abierto.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OwnerVerificationTest {

    static final FakeAuthServer auth = new FakeAuthServer();

    @DynamicPropertySource
    static void authService(DynamicPropertyRegistry r) {
        r.add("minibank.services.auth-url", auth::url);
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    CircuitBreakerRegistry circuitBreakers;

    @BeforeEach
    void reset() {
        auth.reset();
        circuitBreakers.circuitBreaker("authService").reset();
    }

    @Test
    void anActiveUserGetsAVerifiedFavoriteAndAuthReceivesTheUserTokenAndTheCaller() throws Exception {
        String token = TestTokens.forCustomer(1);
        addFavorite(token, "Primo").andExpect(status().isCreated()).andExpect(jsonPath("$.ownerVerified").value(true));

        assertThat(auth.hits).hasValue(1);
        assertThat(auth.authorizations).containsExactly("Bearer " + token);
        assertThat(auth.callers).containsExactly("customer-service");
    }

    @Test
    void aBlockedUserCannotAddFavorites403() throws Exception {
        auth.behaviour = ex -> FakeAuthServer.reply(ex, 200, FakeAuthServer.ANA_BLOCKED);

        addFavorite(TestTokens.forCustomer(1), "Primo").andExpect(status().isForbidden());
        favoriteCount(2);
    }

    @Test
    void aUserThatNoLongerExistsIs404AndIsNotRetried() throws Exception {
        auth.behaviour = ex -> FakeAuthServer.reply(ex, 404, "");

        addFavorite(TestTokens.forCustomer(1), "Primo").andExpect(status().isNotFound());
        assertThat(auth.hits).hasValue(1);
        favoriteCount(2);
    }

    @Test
    void whenAuthFailsTheCallIsRetriedOnceAndThenTheFallbackAcceptsTheFavoriteUnverified() throws Exception {
        auth.behaviour = ex -> FakeAuthServer.reply(ex, 500, "");

        addFavorite(TestTokens.forCustomer(1), "Primo")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerVerified").value(false));
        assertThat(auth.hits).hasValue(2);
    }

    @Test
    void whenAuthIsSlowTheTimeoutTriggersTheFallback() throws Exception {
        auth.behaviour = ex -> {
            Thread.sleep(1500);
            FakeAuthServer.reply(ex, 200, FakeAuthServer.ANA_ACTIVE);
        };

        addFavorite(TestTokens.forCustomer(1), "Primo")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerVerified").value(false));
    }

    @Test
    void whenAuthAnswers503TheFavoriteIsAcceptedUnverifiedAndReadsDoNotDependOnAuth() throws Exception {
        auth.behaviour = ex -> FakeAuthServer.reply(ex, 503, "");

        addFavorite(TestTokens.forCustomer(1), "Primo").andExpect(jsonPath("$.ownerVerified").value(false));
        int callsSoFar = auth.hits.get();
        mvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.forCustomer(1)))
                .andExpect(status().isOk());
        assertThat(auth.hits).hasValue(callsSoFar);
    }

    @Test
    void aRejectionFromAuthIsNotRetriedNorCountedAsAFailure() throws Exception {
        auth.behaviour = ex -> FakeAuthServer.reply(ex, 401, "");

        addFavorite(TestTokens.forCustomer(1), "Primo")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerVerified").value(false));
        assertThat(auth.hits).hasValue(1);
        assertThat(circuitBreakers.circuitBreaker("authService").getMetrics().getNumberOfFailedCalls()).isZero();
    }

    @Test
    void afterEnoughFailuresTheCircuitOpensAndAuthIsNoLongerCalled() throws Exception {
        auth.behaviour = ex -> FakeAuthServer.reply(ex, 500, "");
        for (int i = 0; i < 3; i++) {          // 3 altas x 2 intentos = 6 fallos: supera el mínimo de 5 y el 50 %
            addFavorite(TestTokens.forCustomer(1), "Primo" + i).andExpect(status().isCreated());
        }
        assertThat(circuitBreakers.circuitBreaker("authService").getState().name()).isEqualTo("OPEN");

        int callsWhenOpen = auth.hits.get();
        addFavorite(TestTokens.forCustomer(1), "Otro")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerVerified").value(false));
        assertThat(auth.hits).hasValue(callsWhenOpen);
    }

    @Test
    void localRulesAreCheckedBeforeAskingAuth() throws Exception {
        addFavorite(TestTokens.forCustomer(1), "luis").andExpect(status().isUnprocessableContent());
        assertThat(auth.hits).hasValue(0);
    }

    private ResultActions addFavorite(String token, String alias) throws Exception {
        String account = String.valueOf(Math.abs(alias.hashCode()) % 1_000_000_000L + 1_000_000_000L);
        return mvc.perform(post("/favorites").header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"alias\":\"" + alias + "\",\"accountNumber\":\"" + account + "\",\"bank\":\"BBVA\",\"holder\":\"Juan Diaz\"}"));
    }

    private void favoriteCount(int expected) throws Exception {
        mvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.forCustomer(1)))
                .andExpect(jsonPath("$.length()").value(expected));
    }
}
