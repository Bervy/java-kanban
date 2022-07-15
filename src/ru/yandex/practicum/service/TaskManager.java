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

    boolean isAddTask(Task newTask);

    boolean isUpdateTask(Task updatedTask);

    boolean isRemoveTask(int id);

    List<Epic> getEpics();

    void removeAllEpics();

    Epic getEpic(int id);

    boolean isAddEpic(Epic newEpic);

    boolean isUpdateEpic(Epic updatedEpic);

    boolean isRemoveEpic(int id);

    List<Integer> getSubTasksOfEpic(Epic epic);

    List<SubTask> getSubTasks();

    void removeAllSubTasks();

    SubTask getSubTask(int id);

    boolean isAddSubTask(SubTask newSubtask);

    boolean isUpdateSubTask(SubTask updatedSubTask);

    boolean isRemoveSubTask(Integer id);

    List<Task> getHistory();

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();
}