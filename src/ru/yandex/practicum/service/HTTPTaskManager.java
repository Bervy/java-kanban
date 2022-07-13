package ru.yandex.practicum.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.InMemoryHistoryManagerAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.server.KVTaskClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Vlad Osipov
 * @create 2022-07-10   23:35
 */
public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private String key;

    public HTTPTaskManager(String url, int port, String key) {
        kvTaskClient = new KVTaskClient(url, port);
        this.key = key;
    }

    @Override
    protected void save() {
        String toJson = getNewGson().toJson(this);
        kvTaskClient.put(key, toJson);
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Gson getNewGson() {
        return new GsonBuilder().
                registerTypeAdapter(Duration.class, new DurationAdapter()).
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                registerTypeAdapter(HistoryManager.class, new InMemoryHistoryManagerAdapter(this)).
                setPrettyPrinting().create();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HTTPTaskManager)) return false;
        if (!super.equals(o)) return false;
        HTTPTaskManager that = (HTTPTaskManager) o;
        return Objects.equals(kvTaskClient, that.kvTaskClient) && Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kvTaskClient, key);
    }
}