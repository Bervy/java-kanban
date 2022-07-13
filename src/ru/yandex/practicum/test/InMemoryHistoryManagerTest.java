package ru.yandex.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.service.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Vlad Osipov
 * @create 2022-06-28   8:29
 */
class InMemoryHistoryManagerTest {

    private TaskManager inMemoryTaskManager;

    @BeforeEach
    public void setup() {
        inMemoryTaskManager = Managers.getInMemory();
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T06:00:00");
        Task task2 = new Task(
                "Task2",
                "Task2",
                "PT30M",
                "2022-08-30T08:00:00");
        Epic epic1 = new Epic(
                "Epic1",
                "Epic1");
        SubTask subTask1 = new SubTask(
                3, "SubTask1",
                "SubTask1",
                "PT30M",
                "2022-08-30T10:00:00");
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.addSubTask(subTask1);
    }

    @Test
    void shouldAddTaskToEmptyHistory() {
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getSubTask(4);
        List<Task> history = inMemoryTaskManager.getHistory();
        assertNotNull(history, "History don't return");
        int expected = 2;
        assertEquals(expected, history.size(), "History size not equal");
    }

    @Test
    void addTaskWithSameTaskInHistory() {
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(1);
        List<Task> history = inMemoryTaskManager.getHistory();
        assertNotNull(history, "History don't return");
        int expected = 1;
        assertEquals(expected, history.size(), "History size not equal");
    }

    @Test
    void shouldRemoveFromBegin() {
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getHistoryManager().remove(1);
        List<Task> history = inMemoryTaskManager.getHistory();
        assertNotNull(history, "History don't return");
        int expected = 2;
        assertEquals(expected, history.size(), "History size not equal");
    }

    @Test
    void shouldRemoveFromMiddle() {
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getHistoryManager().remove(2);
        List<Task> history = inMemoryTaskManager.getHistory();
        assertNotNull(history, "History don't return");
        int expected = 2;
        assertEquals(expected, history.size(), "History size not equal");
    }

    @Test
    void shouldRemoveFromEnd() {
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.getTask(2);
        inMemoryTaskManager.getEpic(3);
        inMemoryTaskManager.getHistoryManager().remove(3);
        List<Task> history = inMemoryTaskManager.getHistory();
        assertNotNull(history, "History don't return");
        int expected = 2;
        assertEquals(expected, history.size(), "History size not equal");
    }

    @Test
    void shouldReturnHistory() {
        inMemoryTaskManager.getTask(1);
        List<Task> history = inMemoryTaskManager.getHistory();
        assertNotNull(history, "History don't return");
    }
}