package com.project.minibank;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

// Un auth-service falso (servidor HTTP de la JDK) cuya respuesta elige cada test. Cuenta las llamadas que le llegan.
final class FakeAuthServer {

    interface Behaviour {
        void answer(HttpExchange exchange) throws Exception;
    }

    static final String ANA_ACTIVE = "{\"username\":\"demo\",\"customerId\":1,\"role\":\"CUSTOMER\",\"blocked\":false}";
    static final String ANA_BLOCKED = "{\"username\":\"demo\",\"customerId\":1,\"role\":\"CUSTOMER\",\"blocked\":true}";

    final AtomicInteger hits = new AtomicInteger();
    final List<String> authorizations = new CopyOnWriteArrayList<>();
    final List<String> callers = new CopyOnWriteArrayList<>();
    volatile Behaviour behaviour = ex -> reply(ex, 200, ANA_ACTIVE);
    private final HttpServer server;

    FakeAuthServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        server.createContext("/", exchange -> {
            hits.incrementAndGet();
            authorizations.add(String.valueOf(exchange.getRequestHeaders().getFirst("Authorization")));
            callers.add(String.valueOf(exchange.getRequestHeaders().getFirst("X-Caller-Service")));
            try {
                behaviour.answer(exchange);
            } catch (Exception e) {
                exchange.close();
            }
        });
        server.start();
    }

    String url() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    void reset() {
        hits.set(0);
        authorizations.clear();
        callers.clear();
        behaviour = ex -> reply(ex, 200, ANA_ACTIVE);
    }

    static void reply(HttpExchange exchange, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            exchange.getResponseBody().write(bytes);
        }
        exchange.close();
    }
}
