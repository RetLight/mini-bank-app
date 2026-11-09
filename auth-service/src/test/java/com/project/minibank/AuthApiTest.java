package com.project.minibank;

import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Login de punta a punta: controlador, Spring Security, migraciones Flyway y datos de prueba sobre H2.
@SpringBootTest
@AutoConfigureMockMvc
class AuthApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void validCredentialsReturnATokenWithTheClaimsTheOtherServicesRead() throws Exception {
        String body = login("demo", "demo123")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(1800))
                .andReturn().getResponse().getContentAsString();

        Claims jwt = TestJwt.claims(JsonPath.read(body, "$.token"));
        assertThat(jwt.getSubject()).isEqualTo("demo");
        assertThat(jwt.getIssuer()).isEqualTo("minibank-api");
        assertThat(jwt.get("customerId", Long.class)).isEqualTo(1L);
        assertThat(jwt.get("roles", java.util.List.class)).containsExactly("CUSTOMER");
    }

    @Test
    void wrongPasswordIsRejectedWith401() throws Exception {
        login("demo", "wrong-password").andExpect(status().isUnauthorized());
    }

    @Test
    void unknownUserIsRejectedWith401() throws Exception {
        login("nobody", "demo123").andExpect(status().isUnauthorized());
    }

    @Test
    void blockedUserIsRejectedWith403() throws Exception {
        login("pedro", "pedro123")
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void blankUsernameIsRejectedWith400() throws Exception {
        login("", "demo123").andExpect(status().isBadRequest());
    }

    @Test
    void anyOtherEndpointNeedsAToken() throws Exception {
        mvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    private ResultActions login(String username, String password) throws Exception {
        return mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"));
    }
}
