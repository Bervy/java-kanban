package ru.yandex.practicum.service;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.util.List;

/**
 * @author Vlad Osipov
 * @create 2022-05-09   14:39
 */
public interface TaskManager {

    List<Task> getTasks();

    void removeAllTasks();

    Task getTask(int id);

    void addTask(Task newTask);

    void updateTask(Task updatedTask);

    void removeTask(int id);

    List<Epic> getEpics();

    void removeAllEpics();

    Epic getEpic(int id);

    void addEpic(Epic newEpic);

    void updateEpic(Epic updatedEpic);

    void removeEpic(int id);

    List<Integer> getSubTasksOfEpic(Epic epic);

    List<SubTask> getSubTasks();

    void removeAllSubTasks();

    SubTask getSubTask(int id);

    void addSubTask(SubTask newSubtask);

    void updateSubTask(SubTask updatedSubTask);

    void removeSubTask(Integer id);

    List<Task> getHistory();

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();
}