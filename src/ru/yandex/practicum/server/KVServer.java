package ru.yandex.practicum.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.exceptions.ServerCreateException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Vlad Osipov
 * @create 2022-07-10   18:36
 */
public class KVServer {
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer(int port) {
        apiToken = generateApiToken();
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", port), 0);
        } catch (IOException e) {
            throw new ServerCreateException("Can't create server");
        }
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {
        try (h) {
            if (!hasAuth(h)) {
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                if (data.containsKey(key)) {
                    h.sendResponseHeaders(200, 0);
                    byte[] value = data.get(key).getBytes(UTF_8);
                    h.getResponseBody().write(value);
                } else {
                    h.sendResponseHeaders(400, 0);
                }
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void save(HttpExchange h) throws IOException {
        try (h) {
            if (!hasAuth(h)) {
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                h.sendResponseHeaders(200, 0);
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    private void register(HttpExchange h) throws IOException {
        try (h) {
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                h.sendResponseHeaders(405, 0);
            }
        }
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}