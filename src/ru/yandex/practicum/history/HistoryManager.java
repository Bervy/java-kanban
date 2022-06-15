package ru.yandex.practicum.history;

import ru.yandex.practicum.task.Task;

import java.util.List;

/**
 * @author Vlad Osipov
 * @create 2022-05-09   19:05
 */
public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
