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
    private String fileName = "savedTasks.csv";

    protected void save() {
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
    public boolean addTask(Task newTask) {
        if (super.addTask(newTask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        if (super.updateTask(updatedTask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeTask(int id) {
        if (super.removeTask(id)) {
            save();
            return true;
        }
        return false;
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
    public boolean addEpic(Epic newEpic) {
        if (super.addEpic(newEpic)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic updatedEpic) {
        if (super.updateEpic(updatedEpic)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEpic(int id) {
        if (super.removeEpic(id)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public boolean addSubTask(SubTask newSubtask) {
        if (super.addSubTask(newSubtask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSubTask(SubTask updatedSubTask) {
        if (super.updateSubTask(updatedSubTask)) {
            save();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubTask(Integer id) {
        if (super.removeSubTask(id)) {
            save();
            return true;
        }
        return false;
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