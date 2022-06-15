package ru.yandex.practicum.service;

import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.task.TaskType;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Vlad Osipov
 * @create 2022-06-13   12:20
 */
public class FileBackedTasksManager extends InMemoryTaskManager {
    public static final String FILENAME = "savedTasks.csv";

    private void save() {
        try (Writer fileWriter = new FileWriter(FILENAME)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : getListOfTasks()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : getListOfEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (SubTask subTask : getListOfSubTasks()) {
                fileWriter.write(subTask.toString() + "\n");
            }
            fileWriter.write("\n");
            for (Task task : getHistory()) {
                fileWriter.write(task.getId() + ",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось сохранить в файл " + e.getMessage());
        }
    }

    public void addTasksFromFile(Task taskFromFile) {
        if(taskFromFile.getTaskType() == TaskType.TASK) {
            tasks.put(taskFromFile.getId(), taskFromFile);
        } else if(taskFromFile.getTaskType() == TaskType.EPIC) {
            epics.put(taskFromFile.getId(), (Epic) taskFromFile);
        } else {
            subTasks.put(taskFromFile.getId(), (SubTask) taskFromFile);
        }
     }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task foundedTask = super.getTaskById(id);
        if (foundedTask != null) {
            save();
            return foundedTask;
        } else {
            return null;
        }
    }

    @Override
    public void addTask(Task newTask) {
        super.addTask(newTask);
        save();
    }

    @Override
    public void updateTask(Task updatedTask) {
        super.updateTask(updatedTask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic foundedEpic = super.getEpicById(id);
        if (foundedEpic != null) {
            save();
            return foundedEpic;
        } else {
            return null;
        }
    }

    @Override
    public void addEpic(Epic newEpic) {
        super.addEpic(newEpic);
        save();
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void addSubTask(SubTask newSubtask) {
        super.addSubTask(newSubtask);
        save();
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        super.updateSubTask(updatedSubTask);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        if (subTask != null) {
            save();
            return subTask;
        } else {
            return null;
        }
    }
}