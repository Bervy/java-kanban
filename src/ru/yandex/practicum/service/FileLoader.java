package ru.yandex.practicum.service;

import ru.yandex.practicum.exceptions.ManagerLoadException;
import ru.yandex.practicum.task.TaskType;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Vlad Osipov
 * @create 2022-06-14   16:27
 */
public class FileLoader {

    private FileLoader() {

    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getName()))) {
            while (bufferedReader.ready()) {
                String taskLine = bufferedReader.readLine();
                if (!taskLine.isEmpty()) {
                    tasksFromString(taskLine, fileBackedTasksManager);
                } else {
                    String historyLine = bufferedReader.readLine();
                    historyFromString(historyLine, fileBackedTasksManager);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Не удалось загрузить файл " + e.getMessage());
        }
        return fileBackedTasksManager;
    }

    public static void tasksFromString(String taskLine, FileBackedTasksManager fileBackedTasksManager) {
        String[] taskFields = taskLine.split(",");
        try{
            TaskType taskType = TaskType.valueOf(taskFields[1]);
            switch(taskType) {
                case TASK:
                    Task task = new Task(taskLine);
                    fileBackedTasksManager.addTasksFromFile(task);
                    break;
                case EPIC:
                    Epic epic = new Epic(taskLine);
                    fileBackedTasksManager.addTasksFromFile(epic);
                    break;
                case SUBTASK:
                    SubTask subTask = new SubTask(taskLine);
                    fileBackedTasksManager.addTasksFromFile(subTask);
                    break;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Неверный тип задачи.");
        }
    }

    public static void historyFromString(String historyLine, FileBackedTasksManager fileBackedTasksManager) {
        String[] historyFields = historyLine.split(",");
        for (String field : historyFields) {
            int id = Integer.parseInt(field);
            fileBackedTasksManager.getTaskById(id);
            fileBackedTasksManager.getEpicById(id);
            fileBackedTasksManager.getSubTaskById(id);
        }
    }
}