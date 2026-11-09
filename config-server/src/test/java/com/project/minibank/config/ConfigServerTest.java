package com.project.minibank.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Arranca el Config Server real contra la carpeta config-repo del repositorio.
@SpringBootTest(properties = "spring.cloud.config.server.native.search-locations=file:../config-repo")
@AutoConfigureMockMvc
class ConfigServerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void servesTheBaseFileOfAService() throws Exception {
        mvc.perform(get("/account-service/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertySources[*].source['server.port']").value(org.hamcrest.Matchers.hasItem("8083")));
    }

    @Test
    void theDevProfileAddsTheSeedLocationAndWinsOverTheBaseFile() throws Exception {
        mvc.perform(get("/auth-service/dev"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertySources[0].name").value(org.hamcrest.Matchers.endsWith("auth-service-dev.properties")))
                .andExpect(jsonPath("$.propertySources[0].source['spring.flyway.locations']").value("classpath:db/migration,classpath:db/seed"));
    }

    @Test
    void everyServiceHasItsFiles() throws Exception {
        for (String service : List.of("auth-service", "customer-service", "account-service", "api-gateway")) {
            mvc.perform(get("/" + service + "/dev"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.propertySources[*].name").value(org.hamcrest.Matchers.hasItem(org.hamcrest.Matchers.endsWith(service + ".properties"))));
        }
    }

    // Perfiles dev,docker (docker compose --profile app): docker es el último, así que gana, y cambia localhost por
    // los nombres de servicio de Compose; dev sigue aportando los datos de prueba.
    @Test
    void theDockerProfileUsesComposeServiceNames() throws Exception {
        mvc.perform(get("/customer-service/dev,docker"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertySources[0].name").value(org.hamcrest.Matchers.endsWith("customer-service-docker.properties")))
                .andExpect(jsonPath("$.propertySources[0].source['spring.datasource.url']").value("jdbc:mysql://mysql:3306/customer_db"))
                .andExpect(jsonPath("$.propertySources[0].source['minibank.services.auth-url']").value("http://auth-service:8081"))
                .andExpect(jsonPath("$.propertySources[*].source['spring.flyway.locations']").value(org.hamcrest.Matchers.hasItem("classpath:db/migration,classpath:db/seed")));
        for (String service : List.of("auth-service", "account-service", "api-gateway")) {
            mvc.perform(get("/" + service + "/dev,docker"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.propertySources[0].name").value(org.hamcrest.Matchers.endsWith(service + "-docker.properties")));
        }
    }

    // Los secretos (JWT_SECRET, DB_PASSWORD) llegan por variables de entorno, nunca desde config-repo.
    @Test
    void theConfigRepoHoldsNoSecrets() throws IOException {
        try (Stream<Path> files = Files.list(Path.of("../config-repo"))) {
            List<String> offending = files.filter(p -> p.toString().endsWith(".properties"))
                    .flatMap(p -> {
                        try {
                            return Files.readAllLines(p).stream()
                                    .filter(l -> !l.startsWith("#"))
                                    .filter(l -> l.toLowerCase().matches(".*(password|secret|token)[^=]*=.*"))
                                    .map(l -> p.getFileName() + ": " + l);
                        } catch (IOException e) {
                            throw new IllegalStateException(e);
                        }
                    })
                    .toList();
            assertThat(offending).isEmpty();
        }
    }
}
