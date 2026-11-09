package com.project.minibank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// customer-service solo confía en la firma del token: el cliente sale del claim customerId, sin llamar a auth-service.
// @Transactional deshace al terminar cada test lo que escribió, así ninguno depende del orden.
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FavoriteApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void withoutATokenTheRequestIsRejectedWith401() throws Exception {
        mvc.perform(get("/favorites")).andExpect(status().isUnauthorized());
    }

    @Test
    void the401UsesTheCommonErrorFormatAndAsksForABearerToken() throws Exception {
        mvc.perform(get("/favorites"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.WWW_AUTHENTICATE, "Bearer"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Falta el token o no es válido"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void anAdminListsAllCustomers() throws Exception {
        mvc.perform(get("/admin/customers").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.forAdmin(2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].email").value("ana.torres@mail.com"))
                .andExpect(jsonPath("$[0].favoritesCount").value(2))
                .andExpect(jsonPath("$[3].status").value("BLOCKED"));
    }

    @Test
    void aCustomerCannotUseAdminRoutes403() throws Exception {
        mvc.perform(get("/admin/customers").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void aServiceTokenCannotUseCustomerRoutes403() throws Exception {
        mvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.forService()))
                .andExpect(status().isForbidden());
    }

    @Test
    void aUserTokenWithoutCustomerIdIsRejectedWith401() throws Exception {
        mvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.customerWithoutCustomerId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminRoutesWithoutATokenGive401() throws Exception {
        mvc.perform(get("/admin/customers")).andExpect(status().isUnauthorized());
    }

    @Test
    void aTokenSignedWithAnotherSecretIsRejectedWith401() throws Exception {
        mvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.signedWithAnotherSecret(1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void anExpiredTokenIsRejectedWith401() throws Exception {
        mvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.expired(1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void theCustomerSeesOnlyTheirOwnFavorites() throws Exception {
        mvc.perform(get("/favorites").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].alias", containsInAnyOrder("Luis", "Maria")));
    }

    @Test
    void aFavoriteOfAnotherCustomerIsNotFound() throws Exception {
        mvc.perform(get("/favorites/3").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addingAFavoriteReturns201() throws Exception {
        mvc.perform(post("/favorites").header(HttpHeaders.AUTHORIZATION, bearer(3))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alias\":\"Ana\",\"accountNumber\":\"00219100000000000001\",\"bank\":\"BCP\",\"holder\":\"Ana Torres\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.alias").value("Ana"));
    }

    @Test
    void invalidDataIsRejectedWith400() throws Exception {
        mvc.perform(post("/favorites").header(HttpHeaders.AUTHORIZATION, bearer(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"alias\":\"\",\"accountNumber\":\"abc\",\"bank\":\"BCP\",\"holder\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void theOwnerUpdatesTheirFavorite() throws Exception {
        mvc.perform(put("/favorites/1").header(HttpHeaders.AUTHORIZATION, bearer(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favorite("Luis BCP", "00219100000000000003", "BCP", "Luis Ramirez")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.alias").value("Luis BCP"));

        mvc.perform(get("/favorites/1").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(jsonPath("$.alias").value("Luis BCP"));
    }

    @Test
    void updatingAFavoriteOfAnotherCustomerIsNotFound() throws Exception {
        mvc.perform(put("/favorites/3").header(HttpHeaders.AUTHORIZATION, bearer(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favorite("Mio", "00219100000000000001", "BCP", "Ana Torres")))
                .andExpect(status().isNotFound());
        mvc.perform(get("/favorites/3").header(HttpHeaders.AUTHORIZATION, bearer(2)))
                .andExpect(jsonPath("$.alias").value("Ana"));
    }

    @Test
    void updatingToAnAliasAlreadyUsedIsRejectedWith422() throws Exception {
        mvc.perform(put("/favorites/1").header(HttpHeaders.AUTHORIZATION, bearer(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(favorite("maria", "00219100000000000003", "BCP", "Luis Ramirez")))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    void theOwnerDeletesTheirFavorite() throws Exception {
        mvc.perform(delete("/favorites/2").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isNoContent());
        mvc.perform(get("/favorites/2").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletingAFavoriteOfAnotherCustomerIsNotFoundAndKeepsIt() throws Exception {
        mvc.perform(delete("/favorites/3").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isNotFound());
        mvc.perform(get("/favorites/3").header(HttpHeaders.AUTHORIZATION, bearer(2)))
                .andExpect(status().isOk());
    }

    private static String favorite(String alias, String accountNumber, String bank, String holder) {
        return "{\"alias\":\"" + alias + "\",\"accountNumber\":\"" + accountNumber + "\",\"bank\":\"" + bank
                + "\",\"holder\":\"" + holder + "\"}";
    }

    private static String bearer(long customerId) {
        return "Bearer " + TestTokens.forCustomer(customerId);
    }
}
