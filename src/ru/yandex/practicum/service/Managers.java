package ru.yandex.practicum.service;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.history.InMemoryHistoryManager;
/**
 * @author Vlad Osipov
 * @create 2022-05-09   15:42
 */
public interface  Managers {

    static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
