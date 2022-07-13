package ru.yandex.practicum.server;

import ru.yandex.practicum.exceptions.ManagerLoadException;
import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.exceptions.TaskClientRegisterException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author Vlad Osipov
 * @create 2022-07-10   22:24
 */
public class KVTaskClient {
    private final String apiToken;
    String url;
    int port;

    public KVTaskClient(String url, int port) {
        this.port = port;
        this.url = url;
        URI register = URI.create(url + port + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(register)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response;
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TaskClientRegisterException("Can't register");
        }
        apiToken = response.body();
    }

    public void put(String key, String json) {
        URI save = URI.create(url + port + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(save)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response;
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ManagerSaveException("Can't save to server");
        }
        if (response.statusCode() != 200) {
            throw new ManagerSaveException("Can't save to server");
        }
    }

    public String load(String key) {
        URI load = URI.create(url + port + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(load)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response;
        try {
            response = client.send(request, handler);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ManagerLoadException("Can't load from server");
        }
        if (response.body().isEmpty()) {
            throw new ManagerLoadException("Can't load from server");
        }
        return response.body();
    }
}