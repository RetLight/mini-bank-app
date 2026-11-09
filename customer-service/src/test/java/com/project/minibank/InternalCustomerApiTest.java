package com.project.minibank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// POST /internal/customers: solo para el token de servicio de auth-service (rol SERVICE).
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InternalCustomerApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void theServiceTokenCreatesAnActiveCustomer() throws Exception {
        create("Bearer " + TestTokens.forService(), "10000099", "eva@mail.com")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());

        mvc.perform(get("/admin/customers").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.forAdmin(2)))
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[4].email").value("eva@mail.com"))
                .andExpect(jsonPath("$[4].status").value("ACTIVE"));
    }

    @Test
    void aCustomerTokenCannotCreateCustomers403() throws Exception {
        create("Bearer " + TestTokens.forCustomer(1), "10000099", "eva@mail.com").andExpect(status().isForbidden());
    }

    @Test
    void anAdminTokenCannotEither403() throws Exception {
        create("Bearer " + TestTokens.forAdmin(2), "10000099", "eva@mail.com").andExpect(status().isForbidden());
    }

    @Test
    void withoutATokenItIs401() throws Exception {
        mvc.perform(post("/internal/customers").contentType(MediaType.APPLICATION_JSON).content(body("10000099", "eva@mail.com")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aRepeatedDocumentOrEmailIs409() throws Exception {
        create("Bearer " + TestTokens.forService(), "10000001", "otro@mail.com").andExpect(status().isConflict());
        create("Bearer " + TestTokens.forService(), "10000099", "ANA.TORRES@mail.com").andExpect(status().isConflict());
    }

    @Test
    void invalidDataIs400() throws Exception {
        create("Bearer " + TestTokens.forService(), "12", "no-es-un-email").andExpect(status().isBadRequest());
    }

    private ResultActions create(String authorization, String documentNumber, String email) throws Exception {
        return mvc.perform(post("/internal/customers").header(HttpHeaders.AUTHORIZATION, authorization)
                .header("X-Caller-Service", "auth-service")
                .contentType(MediaType.APPLICATION_JSON).content(body(documentNumber, email)));
    }

    private static String body(String documentNumber, String email) {
        return "{\"documentType\":\"DNI\",\"documentNumber\":\"" + documentNumber + "\",\"firstNames\":\"Eva\","
                + "\"lastNames\":\"Rojas\",\"email\":\"" + email + "\",\"phone\":\"999000111\"}";
    }
}
