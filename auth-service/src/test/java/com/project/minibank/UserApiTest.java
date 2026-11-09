package com.project.minibank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// GET /users/me: lo que customer-service pregunta a auth-service antes de guardar un favorito.
@SpringBootTest
@AutoConfigureMockMvc
class UserApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void returnsTheCurrentStateOfTheUserOfTheToken() throws Exception {
        mvc.perform(get("/users/me").header(HttpHeaders.AUTHORIZATION, bearer("demo", 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("demo"))
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.blocked").value(false))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void aBlockedUserIsReportedAsBlocked() throws Exception {
        mvc.perform(get("/users/me").header(HttpHeaders.AUTHORIZATION, bearer("pedro", 4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blocked").value(true));
    }

    @Test
    void aUserThatNoLongerExistsIs404() throws Exception {
        mvc.perform(get("/users/me").header(HttpHeaders.AUTHORIZATION, bearer("ghost", 99)))
                .andExpect(status().isNotFound());
    }

    @Test
    void withoutATokenItIs401() throws Exception {
        mvc.perform(get("/users/me")).andExpect(status().isUnauthorized());
    }

    private String bearer(String username, long customerId) {
        return "Bearer " + io.jsonwebtoken.Jwts.builder().issuer("minibank-api").subject(username)
                .claim("roles", java.util.List.of("CUSTOMER")).claim("customerId", customerId)
                .expiration(new java.util.Date(System.currentTimeMillis() + 300_000))
                .signWith(io.jsonwebtoken.security.Keys.hmacShaKeyFor(
                        TestTokens.SECRET.getBytes(java.nio.charset.StandardCharsets.UTF_8)), io.jsonwebtoken.Jwts.SIG.HS256)
                .compact();
    }
}
