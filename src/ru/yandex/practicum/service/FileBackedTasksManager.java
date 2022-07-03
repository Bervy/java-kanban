package ru.yandex.practicum.service;

import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;
import ru.yandex.practicum.task.TaskType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Vlad Osipov
 * @create 2022-06-13   12:20
 */
public class FileBackedTasksManager extends InMemoryTaskManager {
    public String fileName = "savedTasks.csv";

    private void save() {
        try (Writer fileWriter = new FileWriter(fileName)) {
            fileWriter.write("id,startTime,duration,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                fileWriter.write(task.toString() + "\n");
            }
            for (Epic epic : getEpics()) {
                fileWriter.write(epic.toString() + "\n");
            }
            for (SubTask subTask : getSubTasks()) {
                fileWriter.write(subTask.toString() + "\n");
            }
            fileWriter.write("\n");
            for (Task task : getHistory()) {
                fileWriter.write(task.getId() + ",");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file");
        }
    }

    public void addTasksFromFile(Task taskFromFile) {
        if (taskFromFile.getTaskType() == TaskType.TASK) {
            tasks.put(taskFromFile.getId(), taskFromFile);
        } else if (taskFromFile.getTaskType() == TaskType.EPIC) {
            epics.put(taskFromFile.getId(), (Epic) taskFromFile);
        } else {
            subTasks.put(taskFromFile.getId(), (SubTask) taskFromFile);
            SubTask subTask = (SubTask) taskFromFile;
            epics.get(subTask.getEpicId()).getSubTasks().add(subTask.getId());
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task foundedTask = super.getTask(id);
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
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic foundedEpic = super.getEpic(id);
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
    public void removeEpic(int id) {
        super.removeEpic(id);
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
    public void removeSubTask(Integer id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = super.getSubTask(id);
        if (subTask != null) {
            save();
            return subTask;
        } else {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileBackedTasksManager that = (FileBackedTasksManager) o;
        return tasks.equals(that.tasks) && epics.equals(that.epics)
                && subTasks.equals(that.subTasks)
                && historyManager.equals(that.historyManager);
    }
}