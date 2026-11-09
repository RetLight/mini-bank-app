package com.project.minibank.gateway;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

// El gateway real en un puerto aleatorio, delante de servicios falsos (servidores HTTP de la JDK) que responden
// quiénes son, el path y las cabeceras que recibieron.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayRoutingTest {

    private static final String P = "spring.cloud.gateway.server.webflux.";
    private static final HttpServer auth;
    private static final HttpServer customer;
    private static final HttpServer account;
    private static final int deadPort;

    // Inicializador estático: los servicios falsos deben existir antes de que Spring lea las propiedades dinámicas.
    static {
        try {
            auth = fake("auth");
            customer = fake("customer");
            account = fake("account");
            try (var s = new ServerSocket(0)) {
                deadPort = s.getLocalPort();     // aquí no escucha nadie: un servicio caído
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @AfterAll
    static void stopFakes() {
        auth.stop(0);
        customer.stop(0);
        account.stop(0);
    }

    @DynamicPropertySource
    static void routes(DynamicPropertyRegistry r) {
        route(r, 0, "auth-service", auth.getAddress().getPort(), "/auth/**");
        route(r, 1, "customer-service", customer.getAddress().getPort(), "/favorites/**,/admin/customers/**");
        route(r, 2, "account-service", account.getAddress().getPort(), "/transfers/**,/movements/**");
        route(r, 3, "dead-service", deadPort, "/dead/**");
        r.add("minibank.gateway.public-paths", () -> "/auth/**");
    }

    @Value("${local.server.port}")
    int port;

    private final HttpClient http = HttpClient.newHttpClient();

    @Test
    void routesEachPathToItsServiceWithoutRewritingIt() throws Exception {
        for (var c : new String[][]{{"/auth/login", "auth"}, {"/favorites/3", "customer"}, {"/admin/customers", "customer"},
                {"/transfers?accountId=1", "account"}, {"/movements/1", "account"}}) {
            var res = send(request(c[0]).header("Authorization", "Bearer x"));
            assertThat(res.statusCode()).isEqualTo(200);
            assertThat(res.body()).startsWith(c[1] + " GET " + c[0].split("\\?")[0]);
        }
    }

    @Test
    void loginAndRegisterArePublic() throws Exception {
        assertThat(send(request("/auth/login")).statusCode()).isEqualTo(200);
        assertThat(send(request("/auth/register")).statusCode()).isEqualTo(200);
    }

    @Test
    void withoutBearerTheGatewayAnswers401InTheCommonFormatAndTheServiceIsNotCalled() throws Exception {
        var res = send(request("/favorites"));
        assertThat(res.statusCode()).isEqualTo(401);
        assertThat(res.headers().firstValue("Content-Type")).hasValueSatisfying(v -> assertThat(v).contains("application/json"));
        assertThat(res.body()).contains("\"status\":401").contains("\"message\":").containsPattern("\"timestamp\":\"\\d{4}-\\d{2}-\\d{2}T");
    }

    @Test
    void internalRoutesAreNotExposed() throws Exception {
        var res = send(request("/internal/customers").header("Authorization", "Bearer x"));
        assertThat(res.statusCode()).isEqualTo(404);
        assertThat(res.body()).contains("\"status\":404");
    }

    @Test
    void aServiceThatIsDownGives503() throws Exception {
        var res = send(request("/dead/x").header("Authorization", "Bearer x"));
        assertThat(res.statusCode()).isEqualTo(503);
        assertThat(res.body()).contains("\"status\":503");
    }

    @Test
    void addsARequestIdTowardsTheServiceAndBackToTheClient() throws Exception {
        var res = send(request("/movements").header("Authorization", "Bearer x"));
        String id = res.headers().firstValue("X-Request-Id").orElseThrow();
        assertThat(res.body()).contains("x-request-id=" + id);

        var reused = send(request("/movements").header("Authorization", "Bearer x").header("X-Request-Id", "abc-123"));
        assertThat(reused.headers().firstValue("X-Request-Id")).hasValue("abc-123");

        var unsafe = send(request("/movements").header("Authorization", "Bearer x").header("X-Request-Id", "bad id;drop"));
        assertThat(unsafe.headers().firstValue("X-Request-Id")).isNotEqualTo("bad id;drop");
    }

    @Test
    void aClientCannotPretendToBeAService() throws Exception {
        var res = send(request("/favorites").header("Authorization", "Bearer x").header("X-Caller-Service", "auth-service"));
        assertThat(res.body()).doesNotContain("x-caller-service");
    }

    private HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
    }

    private HttpResponse<String> send(HttpRequest.Builder builder) throws Exception {
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static void route(DynamicPropertyRegistry r, int i, String id, int port, String paths) {
        r.add(P + "routes[" + i + "].id", () -> id);
        r.add(P + "routes[" + i + "].uri", () -> "http://localhost:" + port);
        r.add(P + "routes[" + i + "].predicates[0]", () -> "Path=" + paths);
    }

    private static HttpServer fake(String name) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            StringBuilder body = new StringBuilder(name + " " + exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath());
            exchange.getRequestHeaders().forEach((k, v) -> body.append("\n").append(k.toLowerCase()).append("=").append(String.join(",", v)));
            byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });
        server.start();
        return server;
    }
}
