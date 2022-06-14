package ru.yandex.practicum.service;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

/**
 * @author Vlad Osipov
 * @create 2022-05-09   14:39
 */
public interface TaskManager {

    List<Task> getListOfTasks();

    void removeAllTasks();

    Task getTaskById(int taskId);

    void addTask(Task newTask);

    void updateTask(Task updatedTask);

    void removeTaskById(int taskId);

    List<Epic> getListOfEpics();

    void removeAllEpics();

    Epic getEpicById(int epicId);

    void addEpic(Epic newEpic);

    void updateEpic(Epic updatedEpic);

    void removeEpicById(int epicId);

    List<Integer> getSubTasksOfEpic(Epic epic);

    List<SubTask> getListOfSubTasks();

    void removeAllSubTasks();

    SubTask getSubTaskById(int subTaskId);

    void addSubTask(SubTask newSubtask);

    void updateSubTask(SubTask updatedSubTask);

    void removeSubTaskById(int subTaskId);

    List<Task> getHistory();
}