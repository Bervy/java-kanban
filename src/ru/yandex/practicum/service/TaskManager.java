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

    boolean addTask(Task newTask);

    boolean updateTask(Task updatedTask);

    boolean removeTask(int id);

    List<Epic> getEpics();

    void removeAllEpics();

    Epic getEpic(int id);

    boolean addEpic(Epic newEpic);

    boolean updateEpic(Epic updatedEpic);

    boolean removeEpic(int id);

    List<Integer> getSubTasksOfEpic(Epic epic);

    List<SubTask> getSubTasks();

    void removeAllSubTasks();

    SubTask getSubTask(int id);

    boolean addSubTask(SubTask newSubtask);

    boolean updateSubTask(SubTask updatedSubTask);

    boolean removeSubTask(Integer id);

    List<Task> getHistory();

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();
}