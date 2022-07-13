package ru.yandex.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.ManagerLoadException;
import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.service.FileBackedTasksManager;
import ru.yandex.practicum.service.FileLoader;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.service.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Vlad Osipov
 * @create 2022-06-28   8:23
 */
class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private FileBackedTasksManager fileBackedTaskManager;
    private File file;

    @Override
    FileBackedTasksManager createTaskManager() {
        return (FileBackedTasksManager) Managers.getFileBacked();
    }

    @BeforeEach
    public void filedBackedSetup() {
        fileBackedTaskManager = (FileBackedTasksManager) Managers.getFileBacked();
        String fileName = fileBackedTaskManager.getFileName();
        file = new File(fileName);
        try (Writer fileWriter = new FileWriter(fileName)) {
            fileWriter.write(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testLoadWithEmptyListOfTasks() {
        TaskManager loadedFromFile;
        try {
            loadedFromFile = FileLoader.loadFromFile(file);
        } catch (ManagerLoadException e) {
            loadedFromFile = null;
        }
        assertEquals(fileBackedTaskManager, loadedFromFile, "Objects not equal");
    }

    @Test
    void testSaveAndLoadWitEpicsWithoutSubtasks() {
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T12:00:00");
        Task task2 = new Task(
                "Task2",
                "Task2",
                "PT30M",
                "2022-08-28T12:00:00");
        Epic epic1 = new Epic(
                "Epic1",
                "Epic1");

        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.getTask(1);
        fileBackedTaskManager.getTask(2);
        fileBackedTaskManager.getEpic(3);

        TaskManager loadedFromFile = FileLoader.loadFromFile(file);
        assertEquals(fileBackedTaskManager, loadedFromFile, "Objects not equal");
    }

    @Test
    void testSaveAndLoadWithEmptyListOfHistory() {
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T12:00:00");
        Task task2 = new Task(
                "Task2",
                "Task2",
                "PT30M",
                "2022-08-28T12:00:00");
        Epic epic1 = new Epic(
                "Epic1",
                "Epic1");

        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.addEpic(epic1);

        TaskManager loadedFromFile = FileLoader.loadFromFile(file);
        assertEquals(fileBackedTaskManager, loadedFromFile, "Objects not equal");
    }

    @Test
    void loadWithTasksEpicsSubtasksHistory() {
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T12:00:00");
        Task task2 = new Task(
                "Task2",
                "Task2",
                "PT30M",
                "2022-08-30T15:00:00");
        Epic epic1 = new Epic(
                "Epic1",
                "Epic1");
        SubTask subTask1 = new SubTask(
                3,
                "Subtask1",
                "Subtask1",
                "PT30M",
                "2022-08-30T07:00:00");
        SubTask subTask2 = new SubTask(
                3,
                "Subtask2",
                "Subtask2",
                "PT30M",
                "2022-08-30T09:00:00");
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.addSubTask(subTask1);
        fileBackedTaskManager.addSubTask(subTask2);
        fileBackedTaskManager.getTask(1);
        fileBackedTaskManager.getTask(2);
        fileBackedTaskManager.getEpic(3);
        fileBackedTaskManager.getSubTask(4);
        fileBackedTaskManager.getSubTask(5);
        TaskManager loadedFromFile = FileLoader.loadFromFile(file);
        assertEquals(fileBackedTaskManager, loadedFromFile, "Objects not equal");
    }

    @Test
    void shouldNotLoadFromFile() {
        File failFile = new File("fail.csv");
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T12:00:00");
        Task task2 = new Task(
                "Task2",
                "Task2",
                "PT30M",
                "2022-08-28T12:00:00");
        Epic epic1 = new Epic(
                "Epic1",
                "Epic1");
        fileBackedTaskManager.addTask(task1);
        fileBackedTaskManager.addTask(task2);
        fileBackedTaskManager.addEpic(epic1);
        fileBackedTaskManager.getTask(1);
        fileBackedTaskManager.getTask(2);
        fileBackedTaskManager.getEpic(3);
        Exception exception = assertThrows(ManagerLoadException.class, () -> FileLoader.loadFromFile(failFile));
        assertEquals("Can't load from file", exception.getMessage(), "Exception not thrown");
    }

    @Test
    void shouldNotGetTaskFromString() {
        Task testTask = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T12:00:00");
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("id,startTime,endTime,type,name,status,description,epic\n");
            fileWriter.write(testTask.getId() + "," +
                    testTask.getStartTime().toString() + ","
                    + testTask.getEndTime().toString() +
                    ",TASKK," + testTask.getName() + "," + testTask.getState() +
                    "," + testTask.getDescription());
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file" + e.getMessage());
        }
        Exception exception = assertThrows(ManagerLoadException.class, () -> FileLoader.loadFromFile(file));
        assertEquals("Wrong type of task", exception.getMessage(), "Exception not thrown");
    }

    @Test
    void shouldNotSaveToFile() {
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T12:00:00");
        fileBackedTaskManager.setFileName(".");
        Exception exception = assertThrows(ManagerSaveException.class, () -> fileBackedTaskManager.addTask(task1));
        assertEquals("Can't save to file", exception.getMessage(), "Exception not thrown");
    }
}