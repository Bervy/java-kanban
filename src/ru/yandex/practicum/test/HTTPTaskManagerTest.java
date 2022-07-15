package ru.yandex.practicum.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.InMemoryHistoryManagerAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.exceptions.ManagerLoadException;
import ru.yandex.practicum.exceptions.ManagerSaveException;
import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.server.KVServer;
import ru.yandex.practicum.server.KVTaskClient;
import ru.yandex.practicum.service.HTTPTaskManager;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Vlad Osipov
 * @create 2022-07-11   0:55
 */
public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    private static final int PORT = 8080;
    private static final String URL = "http://localhost:";
    private static final String KEY = "TEST";
    private KVServer server;
    private HTTPTaskManager httpTaskManager;
    private Gson gson;
    private KVTaskClient kvTaskClient;

    @Override
    HTTPTaskManager createTaskManager() {
        server = new KVServer(PORT);
        server.start();
        return (HTTPTaskManager) Managers.getDefault(URL, PORT, KEY);
    }

    @BeforeEach
    public void httpSetup() {
        httpTaskManager = (HTTPTaskManager) Managers.getDefault(URL, PORT, KEY);
        gson = new GsonBuilder().
                registerTypeAdapter(Duration.class, new DurationAdapter()).
                registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
                registerTypeAdapter(HistoryManager.class, new InMemoryHistoryManagerAdapter(httpTaskManager)).
                setPrettyPrinting().create();
        kvTaskClient = new KVTaskClient(URL, PORT);
    }

    @AfterEach
    public void after() {
        server.stop();
    }

    @Test
    void testLoadWithEmptyListOfTasks() {
        String loadedFromServer;
        String expected = gson.toJson(httpTaskManager);
        kvTaskClient.put(KEY, expected);
        loadedFromServer = kvTaskClient.load(KEY);
        assertEquals(expected, loadedFromServer, "Objects not equal");
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
        httpTaskManager.isAddTask(task1);
        httpTaskManager.isAddTask(task2);
        httpTaskManager.isAddEpic(epic1);
        httpTaskManager.getTask(1);
        httpTaskManager.getTask(2);
        httpTaskManager.getEpic(3);
        String loadedFromServer;
        String expected = gson.toJson(httpTaskManager);
        loadedFromServer = kvTaskClient.load(KEY);
        assertEquals(expected, loadedFromServer, "Objects not equal");
    }

    @Test
    void testSaveAndLoadWithEmptyListOfHistory() {
        Task task1 = new Task("Task1",
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
        httpTaskManager.isAddTask(task1);
        httpTaskManager.isAddTask(task2);
        httpTaskManager.isAddEpic(epic1);
        String loadedFromServer;
        String expected = gson.toJson(httpTaskManager);
        loadedFromServer = kvTaskClient.load(KEY);
        assertEquals(expected, loadedFromServer, "Objects not equal");
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
        httpTaskManager.isAddTask(task1);
        httpTaskManager.isAddTask(task2);
        httpTaskManager.isAddEpic(epic1);
        httpTaskManager.isAddSubTask(subTask1);
        httpTaskManager.isAddSubTask(subTask2);
        httpTaskManager.getTask(1);
        httpTaskManager.getTask(2);
        httpTaskManager.getEpic(3);
        httpTaskManager.getSubTask(4);
        httpTaskManager.getSubTask(5);
        String loadedFromServer;
        String expected = gson.toJson(httpTaskManager);
        loadedFromServer = kvTaskClient.load(KEY);
        assertEquals(expected, loadedFromServer, "Objects not equal");
    }

    @Test
    void shouldNotLoadFromServer() {
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
        httpTaskManager.isAddTask(task1);
        httpTaskManager.isAddTask(task2);
        httpTaskManager.isAddEpic(epic1);
        httpTaskManager.getTask(1);
        httpTaskManager.getTask(2);
        httpTaskManager.getEpic(3);
        Exception exception = assertThrows(ManagerLoadException.class, () -> kvTaskClient.load("&!"));
        assertEquals("Can't load from server", exception.getMessage(), "Exception not thrown");
    }

    @Test
    void shouldNotSaveToServer() {
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T12:00:00");
        httpTaskManager.setKey("");
        Exception exception = assertThrows(ManagerSaveException.class, () -> httpTaskManager.isAddTask(task1));
        assertEquals("Can't save to server", exception.getMessage(), "Exception not thrown");
    }
}