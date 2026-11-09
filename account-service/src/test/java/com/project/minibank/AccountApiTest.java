package com.project.minibank;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// account-service valida el token por su cuenta y comprueba la propiedad de cada cuenta con el claim customerId.
// Cada test parte de los datos de prueba: @Transactional deshace al terminar lo que escribió (MockMvc corre en el mismo hilo).
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AccountApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void withoutATokenTheRequestIsRejectedWith401() throws Exception {
        mvc.perform(get("/movements").param("accountId", "1")).andExpect(status().isUnauthorized());
    }

    @Test
    void the401UsesTheCommonErrorFormat() throws Exception {
        mvc.perform(get("/transfers").param("accountId", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void aCustomerCannotUseAdminRoutes403() throws Exception {
        mvc.perform(get("/admin/accounts").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void aTokenSignedWithAnotherSecretIsRejectedWith401() throws Exception {
        mvc.perform(get("/movements").param("accountId", "1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestTokens.signedWithAnotherSecret(1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void theCustomerSeesTheMovementsOfTheirAccount() throws Exception {
        mvc.perform(get("/movements").param("accountId", "1").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("DECREASE"));
    }

    @Test
    void aTransferBetweenMiniBankAccountsMovesTheMoneyAndRecordsBothMovements() throws Exception {
        mvc.perform(post("/transfers").header(HttpHeaders.AUTHORIZATION, bearer(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer(1, "50.00", "00219100000000000004")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.destinationAccountId").value(4));

        mvc.perform(get("/movements").param("accountId", "1").header(HttpHeaders.AUTHORIZATION, bearer(1)))
                .andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/movements").param("accountId", "4").header(HttpHeaders.AUTHORIZATION, bearer(3)))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].type").value("INCREASE"))
                .andExpect(jsonPath("$[0].resultingBalance").value(350.0));
    }

    @Test
    void aTransferFromAnAccountOfAnotherCustomerIsNotFound() throws Exception {
        mvc.perform(post("/transfers").header(HttpHeaders.AUTHORIZATION, bearer(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer(3, "10.00", "00219100000000000004")))
                .andExpect(status().isNotFound());
    }

    @Test
    void aTransferWithoutEnoughBalanceIsRejectedWith422() throws Exception {
        mvc.perform(post("/transfers").header(HttpHeaders.AUTHORIZATION, bearer(1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transfer(1, "5000.00", "00219100000000000004")))
                .andExpect(status().isUnprocessableContent());
    }

    private static String transfer(long sourceAccountId, String amount, String destinationCci) {
        return "{\"sourceAccountId\":" + sourceAccountId + ",\"amount\":" + amount
                + ",\"destinationCci\":\"" + destinationCci + "\",\"destinationBank\":\"BCP\",\"destinationHolder\":\"Maria Lopez\"}";
    }

    private static String bearer(long customerId) {
        return "Bearer " + TestTokens.forCustomer(customerId);
    }
}
