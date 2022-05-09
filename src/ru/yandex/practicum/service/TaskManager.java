package ru.yandex.practicum.service;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Vlad Osipov
 * @create 2022-05-09   14:39
 */
public interface TaskManager {


    List<Task> getListOfTasks ();

    void removeAllTasks ();

    Task getTaskById (int taskId);

    boolean addTask (Task newTask);

    boolean updateTask (Task updatedTask);

    boolean removeTaskById (int taskId);

    List<Epic> getListOfEpics ();

    void removeAllEpics ();

    Epic getEpicById (int epicId);

    boolean addEpic (Epic newEpic);

    boolean updateEpic (Epic updatedEpic);

    boolean removeEpicById (int epicId);

    ArrayList<SubTask> getSubTasksOfEpic (Epic epic);

    List<SubTask> getListOfSubTasks ();

    void removeAllSubTasks ();

    SubTask getSubTaskById (int subTaskId);

    boolean addSubTask (SubTask newSubtask);

    boolean updateSubTask (SubTask updatedSubTask);

    boolean removeSubTaskById (int subTaskId);

    List<Task> getHistory();
}