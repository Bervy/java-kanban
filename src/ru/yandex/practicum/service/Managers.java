package ru.yandex.practicum.service;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.history.InMemoryHistoryManager;

/**
 * @author Vlad Osipov
 * @create 2022-05-09   15:42
 */
public interface Managers {

    static TaskManager getDefault(String url, int port, String key) {
        return new HTTPTaskManager(url, port, key);
    }

    static TaskManager getFileBacked() {
        return new FileBackedTasksManager();
    }

    static TaskManager getInMemory() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}