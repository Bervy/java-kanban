package ru.yandex.practicum.service;

import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Vlad Osipov
 * @create 2022-06-13   12:20
 */
public class FileBackedTasksManager extends InMemoryTaskManager {
    private static final String FILENAME = "savedTasks.csv";

    public static void main(String[] args) {
        testsSixSprint();
        FileBackedTasksManager fileBackedTasksManager = FileLoader.loadFromFile(new File(FILENAME));
        for (Task task : fileBackedTasksManager.getListOfTasks()) {
            System.out.println(task);
        }
        for (Epic epic : fileBackedTasksManager.getListOfEpics()) {
            System.out.println(epic);
        }
        for (SubTask subTask : fileBackedTasksManager.getListOfSubTasks()) {
            System.out.println(subTask);
        }
        System.out.println();
        for (Task task : fileBackedTasksManager.getHistory()) {
            System.out.print(task.getId() + ",");
        }
    }

    public static void testsSixSprint() {
        Task task1 = new Task("Task1", "Task1");
        Task task2 = new Task("Task2", "Task2");

        Epic epic1 = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");

        SubTask subTask1 = new SubTask(3, "subTask1", "subTask1");
        SubTask subTask2 = new SubTask(3, "subTask2", "subTask2");
        SubTask subTask3 = new SubTask(4, "subTask3", "subTask3");

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();

        fileBackedTasksManager.addTask(task1);
        fileBackedTasksManager.addTask(task2);

        fileBackedTasksManager.addEpic(epic1);
        fileBackedTasksManager.addEpic(epic2);

        fileBackedTasksManager.addSubTask(subTask1);
        fileBackedTasksManager.addSubTask(subTask2);
        fileBackedTasksManager.addSubTask(subTask3);

        fileBackedTasksManager.getTaskById(1);
        System.out.println(1 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.getTaskById(2);
        System.out.println(2 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.getEpicById(3);
        System.out.println(3 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.getEpicById(4);
        System.out.println(4 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.getEpicById(4);
        System.out.println(4 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.removeEpicById(4);
        System.out.println(3 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.getSubTaskById(5);
        System.out.println(4 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.getSubTaskById(6);
        System.out.println(5 + " задача(ч) в истории");
        System.out.println(fileBackedTasksManager.getHistory().toString());
        fileBackedTasksManager.getTaskById(1);
        System.out.println(5 + " задач в истории и удалилась первая задача");
        System.out.println(fileBackedTasksManager.getHistory().toString());
    }

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
            try {
                throw new ManagerSaveException("Не удалось сохранить в файл");
            } catch (ManagerSaveException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addTaskFromFile(Task taskFromFile) {
        tasks.put(taskFromFile.getId(), taskFromFile);
    }

    public void addEpicFromFile(Epic epicFromFile) {
        epics.put(epicFromFile.getId(), epicFromFile);
    }

    public void addSubTaskFromFile(SubTask subTaskFromFile) {
        subTasks.put(subTaskFromFile.getId(), subTaskFromFile);
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