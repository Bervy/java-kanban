package ru.yandex.practicum.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.TaskOverlapAnotherTaskException;
import ru.yandex.practicum.service.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.State;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Vlad Osipov
 * @create 2022-06-29   22:33
 */
abstract class TaskManagerTest<T extends TaskManager> {
    private T taskManager;

    abstract T createTaskManager();

    @BeforeEach
    public void setup() {
        taskManager = createTaskManager();
        Task task1 = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T06:00:00");
        Task task2 = new Task(
                "Task2",
                "Task2",
                "PT30M",
                "2022-08-30T14:00:00");
        Epic epic1 = new Epic(
                "Epic1",
                "Epic1");
        Epic epic2 = new Epic(
                "Epic2",
                "Epic2");
        Epic epic3 = new Epic(
                "Epic3",
                "Epic3");
        SubTask subTask1 = new SubTask(
                3, "SubTask1", "SubTask1", "PT30M", "2022-08-30T09:00:00");
        SubTask subTask2 = new SubTask(
                3, "SubTask2",
                "SubTask2",
                "PT30M",
                "2022-08-30T10:00:00");
        SubTask subTask3 = new SubTask(
                4, "SubTask3",
                "SubTask3",
                "PT30M",
                "2022-08-29T09:00:00");
        SubTask subTask4 = new SubTask(
                4, "SubTask4",
                "SubTask4",
                "PT30M",
                "2022-08-28T12:00:00");
        taskManager.isAddTask(task1);
        taskManager.isAddTask(task2);
        taskManager.isAddEpic(epic1);
        taskManager.isAddEpic(epic2);
        taskManager.isAddEpic(epic3);
        taskManager.isAddSubTask(subTask1);
        taskManager.isAddSubTask(subTask2);
        taskManager.isAddSubTask(subTask3);
        taskManager.isAddSubTask(subTask4);
    }

    @Test
    void shouldReturnListOfTasksWithSize2() {
        List<Task> testedListOfTasks = taskManager.getTasks();
        Task expectedTask = taskManager.getTask(1);
        int expectedSize = 2;
        assertNotNull(testedListOfTasks, "Tasks don't return");
        assertEquals(expectedSize, testedListOfTasks.size(), "Wrong tasks size");
        assertEquals(expectedTask, testedListOfTasks.get(0), "Tasks not equal");
    }

    @Test
    void shouldRemoveAllTasks() {
        List<Task> beforeRemoveListOfTasks = taskManager.getTasks();
        int beforeRemoveExpectedSize = 2;
        int afterRemoveExpectedSize = 0;
        taskManager.removeAllTasks();
        List<Task> afterRemoveListOfTasks = taskManager.getTasks();
        assertEquals(beforeRemoveExpectedSize, beforeRemoveListOfTasks.size(), "Wrong tasks size");
        assertEquals(afterRemoveExpectedSize, afterRemoveListOfTasks.size(), "Wrong tasks size");
    }

    @Test
    void shouldReturnTaskWithId1() {
        Task testedTask = taskManager.getTask(1);
        int expectedId = 1;
        assertNotNull(testedTask, "Task not found");
        assertEquals(expectedId, testedTask.getId(), "Tasks not equal");
    }

    @Test
    void shouldNotReturnTaskWithId5() {
        Task testedTask = taskManager.getTask(5);
        assertNull(testedTask, "Task found");
    }

    @Test
    void shouldAddNewTask() {
        Task expectedTask = new Task(
                "Test Task",
                "Test Task",
                "PT30M",
                "2022-08-31T12:00:00");
        taskManager.isAddTask(expectedTask);
        Task testedTask = taskManager.getTask(10);
        assertNotNull(testedTask, "Task not found");
        assertEquals(expectedTask, testedTask, "Tasks not equal");
    }

    @Test
    void shouldNotAddSameTask() {
        int expectedSize = 2;
        assertEquals(expectedSize, taskManager.getTasks().size());
        Task sameTask = taskManager.getTask(1);
        taskManager.isAddTask(sameTask);
        assertEquals(expectedSize, taskManager.getTasks().size());
    }

    @Test
    void shouldUpdateTask() {
        Task expectedTask = new Task(
                "Updated Task",
                "Updated Task",
                "PT30M",
                "2022-08-30T12:00:00");
        expectedTask.setId(1);
        taskManager.isUpdateTask(expectedTask);
        Task testedTask = taskManager.getTask(1);
        assertNotNull(testedTask, "Task not found");
        assertEquals(expectedTask, testedTask, "Tasks not equal");
    }

    @Test
    void shouldNotUpdateTask() {
        Task expectedTask = taskManager.getTask(1);
        taskManager.isUpdateTask(null);
        Task testedTask = taskManager.getTask(1);
        assertNotNull(testedTask, "Task not found");
        assertEquals(expectedTask, testedTask, "Tasks not equal");
    }

    @Test
    void shouldRemoveTaskWithId1() {
        taskManager.isRemoveTask(1);
        Task expectedTask = taskManager.getTask(1);
        assertNull(expectedTask, "Task found");
    }

    @Test
    void shouldNotRemoveTaskWithId3() {
        taskManager.isRemoveTask(3);
        Epic expectedEpic = taskManager.getEpic(3);
        assertNotNull(expectedEpic, "Epic was deleted");
    }

    @Test
    void shouldReturnListOfEpicsWithSize3() {
        List<Epic> testedListOfEpics = taskManager.getEpics();
        int expectedSize = 3;
        Epic expectedEpic = taskManager.getEpic(3);
        assertNotNull(testedListOfEpics, "Epics don't return");
        assertEquals(expectedSize, testedListOfEpics.size(), "Wrong epics size");
        assertEquals(expectedEpic, testedListOfEpics.get(0), "Epics not equal");
    }

    @Test
    void shouldRemoveAllEpics() {
        List<Epic> beforeRemoveListOfEpics = taskManager.getEpics();
        int beforeRemoveExpectedSize = 3;
        int afterRemoveExpectedSize = 0;
        taskManager.removeAllEpics();
        List<Epic> afterRemoveListOfEpics = taskManager.getEpics();
        assertEquals(beforeRemoveExpectedSize, beforeRemoveListOfEpics.size(), "Wrong epics size");
        assertEquals(afterRemoveExpectedSize, afterRemoveListOfEpics.size(), "Wrong epics size");
    }

    @Test
    void shouldReturnEpicWithId3() {
        Epic testedEpic = taskManager.getEpic(3);
        int expectedId = 3;
        assertNotNull(testedEpic, "Epic not found");
        assertEquals(expectedId, testedEpic.getId(), "Epics not equal");
    }

    @Test
    void shouldNotReturnEpicWithId1() {
        Epic testedEpic = taskManager.getEpic(1);
        assertNull(testedEpic, "Epic found");
    }

    @Test
    void shouldAddNewEpic() {
        Epic expectedEpic = new Epic("Test Epic", "Test Epic");
        taskManager.isAddEpic(expectedEpic);
        Epic testedEpic = taskManager.getEpic(10);
        assertNotNull(testedEpic, "Epic not found");
        assertEquals(expectedEpic, testedEpic, "Epics not equal");
    }

    @Test
    void shouldNotAddSameEpic() {
        int expectedSize = 3;
        Epic sameEpic = taskManager.getEpic(3);
        assertEquals(expectedSize, taskManager.getEpics().size());
        taskManager.isAddEpic(sameEpic);
        assertEquals(expectedSize, taskManager.getEpics().size());
    }

    @Test
    void shouldUpdateEpic() {
        Epic expectedEpic = new Epic("Updated Epic", "Updated Epic");
        expectedEpic.setId(3);
        taskManager.isUpdateEpic(expectedEpic);
        Epic testedEpic = taskManager.getEpic(3);
        assertNotNull(testedEpic, "Epic not found");
        assertEquals(expectedEpic, testedEpic, "Epics not equal");
    }

    @Test
    void shouldNotUpdateEpic() {
        Epic expectedEpic = taskManager.getEpic(3);
        taskManager.isUpdateEpic(null);
        Epic testedEpic = taskManager.getEpic(3);
        assertNotNull(testedEpic, "Epic not found");
        assertEquals(expectedEpic, testedEpic, "Epics not equal");
    }

    @Test
    void shouldRemoveEpicWithId3() {
        taskManager.isRemoveEpic(3);
        Epic expectedEpic = taskManager.getEpic(3);
        assertNull(expectedEpic, "Epic was not deleted");
    }

    @Test
    void shouldNotRemoveEpicWithId1() {
        taskManager.isRemoveEpic(1);
        Task expectedTask = taskManager.getTask(1);
        assertNotNull(expectedTask, "Task was deleted");
    }

    @Test
    void shouldReturnListSubtasksOfEpicWithSize2() {
        Epic testEpic = taskManager.getEpic(3);
        SubTask expectedSubtask = taskManager.getSubTask(6);
        List<Integer> testedSubtasksOfEpic = taskManager.getSubTasksOfEpic(testEpic);
        int expectedSize = 2;
        assertNotNull(testedSubtasksOfEpic, "Subtasks don't return");
        assertEquals(expectedSize, testedSubtasksOfEpic.size(), "Wrong subtasks size");
        assertEquals(expectedSubtask.getId(), testedSubtasksOfEpic.get(0), "subtasks not equal");
    }

    @Test
    void shouldReturnListSubtasksOfEpicWithSize0() {
        Epic epicWithoutSubTasks = taskManager.getEpic(5);
        List<Integer> testedSubtasksOfEpic = taskManager.getSubTasksOfEpic(epicWithoutSubTasks);
        int expectedSize = 0;
        assertNotNull(testedSubtasksOfEpic, "Subtasks don't return");
        assertEquals(expectedSize, testedSubtasksOfEpic.size(), "Wrong subtasks size");
    }

    @Test
    void shouldReturnListOfSubtasksWithSize4() {
        List<SubTask> testedListOfSubtasks = taskManager.getSubTasks();
        int expectedSize = 4;
        SubTask expectedSubtask = taskManager.getSubTask(6);
        assertNotNull(testedListOfSubtasks, "Subtasks don't return");
        assertEquals(expectedSize, testedListOfSubtasks.size(), "Wrong subtasks size");
        assertEquals(expectedSubtask, testedListOfSubtasks.get(0), "Subtasks not equal");
    }

    @Test
    void shouldRemoveAllSubtasks() {
        List<SubTask> beforeRemoveListOfSubtasks = taskManager.getSubTasks();
        int beforeRemoveExpectedSize = 4;
        int afterRemoveExpectedSize = 0;
        taskManager.removeAllSubTasks();
        List<SubTask> afterRemoveListOfSubtasks = taskManager.getSubTasks();
        assertEquals(beforeRemoveExpectedSize, beforeRemoveListOfSubtasks.size(), "Wrong subtasks size");
        assertEquals(afterRemoveExpectedSize, afterRemoveListOfSubtasks.size(), "Wrong subtasks size");
    }

    @Test
    void shouldReturnSubtaskWithId6() {
        SubTask expectedSubtask = taskManager.getSubTask(6);
        int expectedId = 6;
        assertNotNull(expectedSubtask, "Subtask not found");
        assertEquals(expectedId, expectedSubtask.getId(), "Epics not equal");
    }

    @Test
    void shouldNotReturnSubtaskWithId1() {
        SubTask testedSubtask = taskManager.getSubTask(1);
        assertNull(testedSubtask, "Epic found");
    }

    @Test
    void shouldAddNewSubtask() {
        SubTask expectedSubtask = new SubTask(
                3,
                "Test Subtask",
                "Test Subtask",
                "PT30M",
                "2022-08-30T07:00:00");
        taskManager.isAddSubTask(expectedSubtask);
        SubTask testedSubtask = taskManager.getSubTask(10);
        assertNotNull(testedSubtask, "Subtask not found");
        assertEquals(expectedSubtask, testedSubtask, "Subtask not equal");
    }

    @Test
    void shouldNotAddSubtaskWithWrongIdInSubTasksOfEpic() {
        SubTask expectedSubtask = new SubTask(
                10,
                "New Subtask",
                "New Subtask",
                "PT30M",
                "2022-08-30T07:00:00");
        taskManager.isAddSubTask(expectedSubtask);
        SubTask testedSubtask = taskManager.getSubTask(6);
        assertNotNull(testedSubtask, "Subtask not found");
        assertNotEquals(expectedSubtask, testedSubtask, "Subtasks is equal");
    }

    @Test
    void shouldNotAddSameSubTask() {
        int expectedSize = 4;
        SubTask sameSubTask = taskManager.getSubTask(6);
        assertEquals(expectedSize, taskManager.getSubTasks().size());
        taskManager.isAddSubTask(sameSubTask);
        assertEquals(expectedSize, taskManager.getSubTasks().size());
    }

    @Test
    void shouldUpdateStartTimeOfEpicAfterAddNewSubtask() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SubTask expectedSubtask = new SubTask(
                3,
                "Test Subtask",
                "Test Subtask",
                "PT300M",
                "2022-08-29T10:00:00");
        taskManager.isAddSubTask(expectedSubtask);
        assertEquals(expectedSubtask.getStartTime().format(dateTimeFormatter), taskManager.getEpic(3).getStartTime().format(dateTimeFormatter), "Start time of epic don't update");
    }

    @Test
    void shouldUpdateSubtask() {
        SubTask expectedSubtask = new SubTask(
                3,
                "Updated Subtask",
                "Updated Subtask",
                "PT30M",
                "2022-08-30T07:00:00");
        expectedSubtask.setId(6);
        taskManager.isUpdateSubTask(expectedSubtask);
        SubTask testedSubtask = taskManager.getSubTask(6);
        assertNotNull(testedSubtask, "Subtask not found");
        assertEquals(expectedSubtask, testedSubtask, "Subtasks not equal");
    }

    @Test
    void shouldUpdateStartTimeOfEpicAfterUpdateSubtask() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        SubTask expectedSubtask = new SubTask(
                3,
                "Test Subtask",
                "Test Subtask",
                "PT30M",
                "2022-08-30T08:00:00");
        expectedSubtask.setId(6);
        taskManager.isUpdateSubTask(expectedSubtask);
        assertEquals(expectedSubtask.getStartTime().format(dateTimeFormatter), taskManager.getEpic(3).getStartTime().format(dateTimeFormatter), "Start time of epic don't update");
    }

    @Test
    void shouldNotUpdateSubtask() {
        SubTask expectedSubtask = taskManager.getSubTask(6);
        taskManager.isUpdateSubTask(null);
        SubTask testedSubtask = taskManager.getSubTask(6);
        assertNotNull(testedSubtask, "Subtask not found");
        assertEquals(expectedSubtask, testedSubtask, "Subtasks not equal");
    }

    @Test
    void shouldNotUpdateSubtaskWithWrongIdInSubTasksOfEpic() {
        SubTask expectedSubtask = new SubTask(
                3,
                "Updated Subtask",
                "Updated Subtask",
                "PT30M",
                "2022-08-30T07:00:00");
        expectedSubtask.setId(6);
        Epic testEpic = taskManager.getEpic(3);
        testEpic.getSubTasks().set(0, 20);
        taskManager.isUpdateSubTask(expectedSubtask);
        SubTask testedSubtask = taskManager.getSubTask(6);
        assertNotNull(testedSubtask, "Subtask not found");
        assertNotEquals(expectedSubtask, testedSubtask, "Subtasks is equal");
    }

    @Test
    void shouldRemoveSubtaskWithId6() {
        taskManager.isRemoveSubTask(6);
        SubTask expectedSubtask = taskManager.getSubTask(6);
        assertNull(expectedSubtask, "Subtask was not deleted");
    }

    @Test
    void shouldUpdateStartTimeOfEpicAfterRemoveSubtask() {
        SubTask removedSubtask = taskManager.getSubTask(6);
        SubTask expectedSubtask = taskManager.getSubTask(7);
        assertEquals(removedSubtask.getStartTime(),
                taskManager.getEpic(3).getStartTime(),
                "Start time of epic don't update");
        taskManager.isRemoveSubTask(6);
        assertEquals(expectedSubtask.getStartTime(),
                taskManager.getEpic(3).getStartTime(),
                "Start time of epic don't update");
    }

    @Test
    void shouldNotRemoveSubtaskWithId1() {
        taskManager.isRemoveSubTask(1);
        Task expectedTask = taskManager.getTask(1);
        assertNotNull(expectedTask, "Task was deleted");
    }

    @Test
    void shouldReturnHistoryWithSize2() {
        taskManager.getTask(1);
        taskManager.getEpic(3);
        List<Task> testedListOfHistory = taskManager.getHistory();
        int expectedSize = 2;
        Epic expectedEpic = taskManager.getEpic(3);
        assertNotNull(testedListOfHistory, "History don't return");
        assertEquals(expectedSize, testedListOfHistory.size(), "Wrong history size");
        assertEquals(expectedEpic, testedListOfHistory.get(1), "Epics not equal");
    }

    @Test
    void shouldReturnHistoryWithSize0() {
        List<Task> testedListOfHistory = taskManager.getHistory();
        int expectedSize = 0;
        assertNotNull(testedListOfHistory, "History don't return");
        assertEquals(expectedSize, testedListOfHistory.size(), "Wrong history size");
    }


    @Test
    void statusEpicNewWithEmptyListOfSubtasks() {
        State expectedState = State.NEW;
        State testState = taskManager.getEpic(5).getState();
        assertEquals(expectedState, testState, "Status of epic is not NEW");
    }

    @Test
    void statusEpicNewWithAllSubtasksNew() {
        State expectedState = State.NEW;
        State testState = taskManager.getEpic(3).getState();
        assertEquals(expectedState, testState, "Status of epic is not NEW");
    }

    @Test
    void statusEpicDoneWithAllSubtasksDone() {
        State expectedState = State.DONE;
        SubTask testSubtask1 = new SubTask(
                3,
                "SubTask1",
                "SubTask1",
                "PT30M",
                "2022-08-30T09:00:00");
        testSubtask1.setState(State.DONE);
        testSubtask1.setId(6);
        taskManager.isUpdateSubTask(testSubtask1);
        SubTask testSubTask2 = new SubTask(
                3,
                "SubTask2",
                "SubTask2",
                "PT30M",
                "2022-08-30T10:00:00");
        testSubTask2.setState(State.DONE);
        testSubTask2.setId(7);
        taskManager.isUpdateSubTask(testSubTask2);
        State testState = taskManager.getEpic(3).getState();
        assertEquals(expectedState, testState, "Status of epic is not DONE");
    }

    @Test
    void statusEpicInProgressWithSubtasksNewAndDone() {
        State expectedState = State.IN_PROGRESS;
        SubTask testSubtask1 = new SubTask(
                3,
                "SubTask1",
                "SubTask1",
                "PT30M",
                "2022-08-30T09:00:00");
        testSubtask1.setState(State.DONE);
        testSubtask1.setId(6);
        taskManager.isUpdateSubTask(testSubtask1);
        State testState = taskManager.getEpic(3).getState();
        assertEquals(expectedState, testState, "Status of epic is not IN_PROGRESS");
    }

    @Test
    void statusEpicInProgressWithAllSubtasksInProgress() {
        State expectedState = State.IN_PROGRESS;
        SubTask testSubtask1 = new SubTask(
                3,
                "SubTask1",
                "SubTask1",
                "PT30M",
                "2022-08-30T09:00:00");
        testSubtask1.setState(State.IN_PROGRESS);
        testSubtask1.setId(6);
        taskManager.isUpdateSubTask(testSubtask1);
        SubTask testSubTask2 = new SubTask(
                3,
                "SubTask2",
                "SubTask2",
                "PT30M",
                "2022-08-30T10:00:00");
        testSubTask2.setState(State.IN_PROGRESS);
        testSubTask2.setId(7);
        taskManager.isUpdateSubTask(testSubTask2);
        State testState = taskManager.getEpic(3).getState();
        assertEquals(expectedState, testState, "Status of epic is not IN_PROGRESS");
    }

    @Test
    void shouldReturnPrioritizedTasksByStartTimeAfterAddNewTask() {
        List<Task> beforeAddNewTaskSortByPriorityTasks = taskManager.getPrioritizedTasks();
        int beforeAddNewTaskSize = 6;
        SubTask beforeAddNewTask = taskManager.getSubTask(9);
        Task beforeAddNewTaskFirstTask = beforeAddNewTaskSortByPriorityTasks.get(0);
        assertEquals(beforeAddNewTaskSize, beforeAddNewTaskSortByPriorityTasks.size(), "Size not equal");
        assertEquals(beforeAddNewTask, beforeAddNewTaskFirstTask, "Task not equal");
        Task expectedTask = new Task(
                "Test Task",
                "Test Task",
                "PT30M",
                "2022-08-27T12:00:00");
        taskManager.isAddTask(expectedTask);
        List<Task> afterAddNewTaskSortByPriorityTasks = taskManager.getPrioritizedTasks();
        Task afterAddNewTask = taskManager.getTask(10);
        Task afterAddNewTaskFirstTask = afterAddNewTaskSortByPriorityTasks.get(0);
        int afterAddNewTaskSize = 7;
        assertEquals(afterAddNewTaskSize, afterAddNewTaskSortByPriorityTasks.size(), "Size not equal");
        assertEquals(afterAddNewTask, afterAddNewTaskFirstTask, "Task not equal");
    }

    @Test
    void shouldNotAddTaskOverlapByTime() {
        Task task = new Task(
                "TestTask",
                "TestTask",
                "PT30M",
                "2022-08-30T06:00:00");
        Task overlapSubTask = taskManager.getTask(1);
        Exception exception = assertThrows(TaskOverlapAnotherTaskException.class, () -> taskManager.isAddTask(task));
        assertEquals("Fail to addTask because " +
                        task.getName() +
                        " overlap " +
                        overlapSubTask.getName(),
                exception.getMessage(),
                "Exception not thrown");
    }

    @Test
    void shouldNotUpdateTaskOverlapByTime() {
        LocalDateTime expectedTimeOfTask = LocalDateTime.parse("2022-08-30T06:00:00");
        Task beforeUpdateTask = taskManager.getTask(1);
        assertEquals(expectedTimeOfTask,
                beforeUpdateTask.getStartTime(),
                "StartTime of task not equal");
        Task updatedTask = new Task(
                "Task1",
                "Task1",
                "PT30M",
                "2022-08-30T09:00:00");
        updatedTask.setId(1);
        SubTask overlapSubTask = taskManager.getSubTask(6);

        Exception exception = assertThrows(
                TaskOverlapAnotherTaskException.class,
                () -> taskManager.isUpdateTask(updatedTask));
        assertEquals(
                "Fail to updateTask because " +
                        updatedTask.getName() +
                        " overlap " +
                        overlapSubTask.getName(),
                exception.getMessage(),
                "Exception not thrown");
        assertNotEquals(
                expectedTimeOfTask,
                updatedTask.getStartTime(),
                "StartTime of task not equal");
    }

    @Test
    void shouldNotAddSubTaskOverlapByTime() {
        SubTask subTask = new SubTask(
                3,
                "TestTask",
                "TestTask",
                "PT30M",
                "2022-08-30T06:00:00");
        Task overlapSubTask = taskManager.getTask(1);
        Exception exception = assertThrows(
                TaskOverlapAnotherTaskException.class,
                () -> taskManager.isAddSubTask(subTask));
        assertEquals(
                "Fail to addSubTask because " +
                        subTask.getName() +
                        " overlap " +
                        overlapSubTask.getName(),
                exception.getMessage(),
                "Exception not thrown");
    }

    @Test
    void shouldNotUpdateSubTaskOverlapByTime() {
        LocalDateTime expectedTimeOfTask = LocalDateTime.parse("2022-08-30T10:00:00");
        SubTask beforeUpdateSubTask = taskManager.getSubTask(7);
        assertEquals(
                expectedTimeOfTask,
                beforeUpdateSubTask.getStartTime(),
                "StartTime of subTasks not equal");
        SubTask updatedSubTask = new SubTask(
                3,
                "SubTask2",
                "SubTask2",
                "PT30M",
                "2022-08-30T09:00:00");
        updatedSubTask.setId(7);
        SubTask overlapSubTask = taskManager.getSubTask(6);

        Exception exception = assertThrows(
                TaskOverlapAnotherTaskException.class,
                () -> taskManager.isUpdateSubTask(updatedSubTask));
        assertEquals(
                "Fail to updateSubTask because " +
                        updatedSubTask.getName() +
                        " overlap " +
                        overlapSubTask.getName(),
                exception.getMessage(),
                "Exception not thrown");
        assertNotEquals(
                expectedTimeOfTask,
                updatedSubTask.getStartTime(),
                "StartTime of subTasks not equal");
    }

    @Test
    void shouldRemoveLastSubTaskOfEpic() {
        SubTask subTask = new SubTask(
                5,
                "TestSubTask",
                "TestSubTask",
                "PT30M",
                "2022-08-26T09:00:00");
        taskManager.isAddSubTask(subTask);
        taskManager.isRemoveSubTask(10);
        Epic epic = taskManager.getEpic(5);
        LocalDateTime expectedStartTimeOfEpic = LocalDateTime.MAX;
        LocalDateTime expectedEndTimeOfEpic = LocalDateTime.MIN;
        assertEquals(expectedStartTimeOfEpic, epic.getStartTime(), "StartTime not equal");
        assertEquals(expectedEndTimeOfEpic, epic.getEndTime(), "EndTime not equal");
    }

    @Test
    void removedSubTaskStartTimeAndEndTimeNotEqualTOEpicsTime() {
        SubTask subTask1 = new SubTask(
                5,
                "TestSubTask1",
                "TestSubTask1",
                "PT30M",
                "2022-08-26T09:00:00");
        SubTask subTask2 = new SubTask(
                5,
                "TestSubTask2",
                "TestSubTask2",
                "PT30M",
                "2022-08-26T10:00:00");
        SubTask subTask3 = new SubTask(
                5,
                "TestSubTask3",
                "TestSubTask3",
                "PT30M",
                "2022-08-26T12:00:00");

        taskManager.isAddSubTask(subTask1);
        taskManager.isAddSubTask(subTask2);
        taskManager.isAddSubTask(subTask3);
        taskManager.isRemoveSubTask(11);
        Epic epic = taskManager.getEpic(5);
        LocalDateTime expectedStartTimeOfEpic = subTask1.getStartTime();
        LocalDateTime expectedEndTimeOfEpic = subTask3.getEndTime();
        assertEquals(expectedStartTimeOfEpic, epic.getStartTime(), "StartTime not equal");
        assertEquals(expectedEndTimeOfEpic, epic.getEndTime(), "EndTime not equal");
    }

    @Test
    void shouldNotAddSubTaskIfExists() {
        SubTask testSubTask = new SubTask(
                3,
                "SubTask1",
                "SubTask1",
                "PT30M",
                "2022-08-30T09:00:00");
        SubTask overlapSubTask = taskManager.getSubTask(6);
        Exception exception = assertThrows(
                TaskOverlapAnotherTaskException.class,
                () -> taskManager.isAddSubTask(testSubTask));
        assertEquals(
                "Fail to addSubTask because " +
                        testSubTask.getName() +
                        " overlap " +
                        overlapSubTask.getName(),
                exception.getMessage(),
                "Exception not thrown");
    }

    @Test
    void shouldReturnEmptyListIfEpicNotExists() {
        Epic testEpic = new Epic(
                "TestEpic",
                "TestEpic");
        List<Integer> subTasksOfEpic = taskManager.getSubTasksOfEpic(testEpic);
        int expectedSize = 0;
        assertEquals(expectedSize, subTasksOfEpic.size());
    }

    @Test
    void endTimeOfTaskIfNoStartTimeEqualsMin() {
        Task testTask = new Task(
                "testTask",
                "testTask");
        LocalDateTime expectedEndTime = LocalDateTime.MIN;
        assertEquals(expectedEndTime, testTask.getEndTime());
    }

    @Test
    void endTimeOfSubTaskIfNoStartTimeEqualsMin() {
        SubTask testSubTask = new SubTask(
                3,
                "testSubTask",
                "testSubTask");
        LocalDateTime expectedEndTime = LocalDateTime.MIN;
        assertEquals(expectedEndTime, testSubTask.getEndTime());
    }
}