package com.project.minibank;

import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Registro de punta a punta con un customer-service falso: qué recibe, qué pasa si rechaza y qué pasa si no responde.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RegisterApiTest {

    static final FakeCustomerService customers = new FakeCustomerService();

    @DynamicPropertySource
    static void customerService(DynamicPropertyRegistry r) {
        r.add("minibank.services.customer-url", customers::url);
    }

    @Autowired
    MockMvc mvc;

    @BeforeEach
    void resetFake() {
        customers.reset();
    }

    @Test
    void createsTheCustomerThenTheUserAndReturnsATokenForTheNewCustomer() throws Exception {
        String body = register("eva", "10000099", "eva@mail.com")
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Claims jwt = TestJwt.claims(JsonPath.read(body, "$.token"));
        assertThat(jwt.getSubject()).isEqualTo("eva");
        assertThat(jwt.get("customerId", Long.class)).isEqualTo(5L);
        assertThat(jwt.get("roles", java.util.List.class)).containsExactly("CUSTOMER");

        login("eva", "Secret123").andExpect(status().isOk());
    }

    @Test
    void callsCustomerServiceAsAServiceWithTheTracingHeaders() throws Exception {
        register("eva", "10000099", "eva@mail.com").andExpect(status().isCreated());

        assertThat(customers.received).hasSize(1);
        var call = customers.received.getFirst();
        assertThat(call.method()).isEqualTo("POST");
        assertThat(call.path()).isEqualTo("/internal/customers");
        assertThat(call.header("X-Caller-Service")).isEqualTo("auth-service");
        assertThat(call.header("X-Request-Id")).isNotBlank();
        assertThat(call.body()).contains("\"documentNumber\":\"10000099\"").doesNotContain("Secret123");

        Claims serviceToken = TestJwt.claims(call.header("Authorization").substring("Bearer ".length()));
        assertThat(serviceToken.getSubject()).isEqualTo("auth-service");
        assertThat(serviceToken.get("roles", java.util.List.class)).containsExactly("SERVICE");
        assertThat(serviceToken).doesNotContainKey("customerId");
    }

    @Test
    void aTakenUsernameIsRejectedWith409WithoutCallingCustomerService() throws Exception {
        register("demo", "10000099", "eva@mail.com").andExpect(status().isConflict());
        assertThat(customers.hits).hasValue(0);
    }

    @Test
    void ifCustomerServiceRejectsTheCustomerTheAnswerIs409AndNoUserIsCreated() throws Exception {
        customers.behaviour = ex -> FakeCustomerService.reply(ex, 409, "{\"status\":409}");

        register("eva", "10000001", "ana.torres@mail.com").andExpect(status().isConflict());
        login("eva", "Secret123").andExpect(status().isUnauthorized());
    }

    @Test
    void ifCustomerServiceFailsTheAnswerIs503AndNoUserIsCreated() throws Exception {
        customers.behaviour = ex -> FakeCustomerService.reply(ex, 500, "");

        register("eva", "10000099", "eva@mail.com")
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.status").value(503));
        login("eva", "Secret123").andExpect(status().isUnauthorized());
    }

    @Test
    void invalidDataIsRejectedWith400WithoutCallingCustomerService() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"e\",\"password\":\"short\",\"documentType\":\"DNI\",\"documentNumber\":\"12\","
                                + "\"firstNames\":\"Eva\",\"lastNames\":\"Rojas\",\"email\":\"no-es-un-email\"}"))
                .andExpect(status().isBadRequest());
        assertThat(customers.hits).hasValue(0);
    }

    private ResultActions register(String username, String documentNumber, String email) throws Exception {
        return mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"Secret123\",\"documentType\":\"DNI\","
                        + "\"documentNumber\":\"" + documentNumber + "\",\"firstNames\":\"Eva\",\"lastNames\":\"Rojas\","
                        + "\"email\":\"" + email + "\",\"phone\":\"999000111\"}"));
    }

    private ResultActions login(String username, String password) throws Exception {
        return mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"));
    }
}
