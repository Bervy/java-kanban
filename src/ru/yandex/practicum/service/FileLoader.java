package ru.yandex.practicum.service;

import ru.yandex.practicum.Tasks;
import ru.yandex.practicum.exceptions.ManagerLoadException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

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
            try {
                throw new ManagerLoadException("Не удалось загрузить из файла ");
            } catch (ManagerLoadException ex) {
                ex.printStackTrace();
            }
        }
        return fileBackedTasksManager;
    }

    private static void tasksFromString(String taskLine, FileBackedTasksManager fileBackedTasksManager) {
        String[] taskFields = taskLine.split(",");
        if (taskFields[1].equals(String.valueOf(Tasks.TASK))) {
            Task task = new Task(taskLine);
            fileBackedTasksManager.addTaskFromFile(task);
        }
        if (taskFields[1].equals(String.valueOf(Tasks.EPIC))) {
            Epic epic = new Epic(taskLine);
            fileBackedTasksManager.addEpicFromFile(epic);
        }
        if (taskFields[1].equals(String.valueOf(Tasks.SUBTASK))) {
            SubTask subTask = new SubTask(taskLine);
            fileBackedTasksManager.addSubTaskFromFile(subTask);
        }
    }

    private static void historyFromString(String historyLine, FileBackedTasksManager fileBackedTasksManager) {
        String[] historyFields = historyLine.split(",");
        for (String field : historyFields) {
            int id = Integer.parseInt(field);
            fileBackedTasksManager.getTaskById(id);
            fileBackedTasksManager.getEpicById(id);
            fileBackedTasksManager.getSubTaskById(id);
        }
    }
}