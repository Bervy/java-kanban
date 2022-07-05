package ru.yandex.practicum.service;

import ru.yandex.practicum.exceptions.TaskOverlapAnotherTaskException;
import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.State;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:00
 */
public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private final TreeSet<Task> tasksSortedByStartTime;
    private Integer generatorId;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.generatorId = 0;
        tasksSortedByStartTime = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Map.Entry<Integer, Task> removedTask : tasks.entrySet()) {
            tasksSortedByStartTime.remove(removedTask.getValue());
            historyManager.remove(removedTask.getKey());
        }
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        Task foundedTask = tasks.get(id);
        if (foundedTask != null) {
            historyManager.add(tasks.get(id));
            return foundedTask;
        } else {
            return null;
        }
    }

    @Override
    public void addTask(Task newTask) {
        if (newTask == null || tasks.containsValue(newTask)
                || isTaskOverlapTasksByTime(newTask, "addTask")) {
            return;
        }
        tasks.put(++generatorId, newTask);
        newTask.setId(generatorId);
        tasksSortedByStartTime.add(newTask);

    }

    @Override
    public void updateTask(Task updatedTask) {
        if (updatedTask != null && tasks.containsKey(updatedTask.getId()) &&
                !isTaskOverlapTasksByTime(updatedTask, "updateTask")) {
            tasks.put(updatedTask.getId(), updatedTask);
            tasksSortedByStartTime.remove(updatedTask);
            tasksSortedByStartTime.add(updatedTask);
        }
    }

    @Override
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasksSortedByStartTime.remove(tasks.get(id));
            tasks.remove(id);
        }
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        for (Map.Entry<Integer, SubTask> removedSubTask : subTasks.entrySet()) {
            tasksSortedByStartTime.remove(removedSubTask.getValue());
            historyManager.remove(removedSubTask.getKey());
        }
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpic(int id) {
        Epic foundedEpic = epics.get(id);
        if (foundedEpic != null) {
            historyManager.add(epics.get(id));
            return foundedEpic;
        } else {
            return null;
        }
    }

    @Override
    public void addEpic(Epic newEpic) {
        if (newEpic == null || epics.containsValue(newEpic)) {
            return;
        }
        epics.put(++generatorId, newEpic);
        newEpic.setId(generatorId);
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (updatedEpic != null && epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    @Override
    public void removeEpic(int id) {
        HashMap<Integer, SubTask> copySubTasks = new HashMap<>(subTasks);
        if (epics.get(id) != null) {
            for (Integer epicSubTaskId : epics.get(id).getSubTasks()) {
                for (SubTask subTask : copySubTasks.values()) {
                    if (epicSubTaskId == subTask.getId()) {
                        historyManager.remove(subTask.getId());
                        subTasks.remove(subTask.getId());
                    }
                }
            }
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    /**
     * @param subTask Подзадача по которой надо найти эпик
     * @return Возвращает эпик, найденный по подзадаче
     */
    private Epic getEpicBySubTask(SubTask subTask) {
        for (Epic epic : epics.values()) {
            if (epic.getSubTasks().contains(subTask.getId())) {
                return epic;
            }
        }
        return null;
    }

    /**
     * @param epic Эпик, для которого надо рассчитать его статус
     *             Метод рассчитывает статус для переданного через параметры эпика
     */
    private void setStatusEpic(Epic epic) {
        ArrayList<Integer> subTasksOfFoundedEpic = new ArrayList<>(epic.getSubTasks());
        int count = 0;

        for (Integer subTaskId : subTasksOfFoundedEpic) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getState() == State.IN_PROGRESS) {
                count++;
            } else if (subTask.getState() == State.DONE) {
                count += 2;
            }
        }
        if (count == 0) {
            epic.setState(State.NEW);
        } else if (count == subTasksOfFoundedEpic.size() * 2) {
            epic.setState(State.DONE);
        } else {
            epic.setState(State.IN_PROGRESS);
        }
    }

    @Override
    public List<Integer> getSubTasksOfEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return epic.getSubTasks();
        }
        return Collections.emptyList();
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        for (Map.Entry<Integer, SubTask> removedSubTask : subTasks.entrySet()) {
            tasksSortedByStartTime.remove(removedSubTask.getValue());
            historyManager.remove(removedSubTask.getKey());
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.setDuration(0);
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask foundedSubTask = subTasks.get(id);
        if (foundedSubTask != null) {
            historyManager.add(subTasks.get(id));
            return foundedSubTask;
        } else {
            return null;
        }
    }

    @Override
    public void addSubTask(SubTask newSubtask) {
        if (newSubtask == null || subTasks.containsValue(newSubtask) ||
                isTaskOverlapTasksByTime(newSubtask, "addSubTask")) {
            return;
        }
        subTasks.put(++generatorId, newSubtask);
        newSubtask.setId(generatorId);
        tasksSortedByStartTime.add(newSubtask);
        Epic foundedEpic = epics.get(newSubtask.getEpicId());
        foundedEpic.getSubTasks().add(newSubtask.getId());
        setDurationStartTimeAndEndTimeOfEpic(foundedEpic, newSubtask);
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        if (updatedSubTask != null && subTasks.containsKey(updatedSubTask.getId()) &&
                !isTaskOverlapTasksByTime(updatedSubTask, "updateSubTask")) {
            Epic foundedEpic = getEpicBySubTask(updatedSubTask);
            if (foundedEpic != null) {
                SubTask oldSubTask = subTasks.get(updatedSubTask.getId());
                foundedEpic.subtractionDuration(oldSubTask.getDuration());
                setDurationStartTimeAndEndTimeOfEpic(foundedEpic, updatedSubTask);
                subTasks.put(updatedSubTask.getId(), updatedSubTask);
                setStatusEpic(foundedEpic);
                tasksSortedByStartTime.remove(updatedSubTask);
                tasksSortedByStartTime.add(updatedSubTask);
            }
        }
    }

    @Override
    public void removeSubTask(Integer id) {
        SubTask removedSubTask = subTasks.remove(id);
        if (removedSubTask != null) {
            tasksSortedByStartTime.remove(removedSubTask);
            historyManager.remove(id);
            Epic foundedEpic = getEpicBySubTask(removedSubTask);
            if (foundedEpic != null) {
                List<Integer> subtasksOfEpic = foundedEpic.getSubTasks();
                subtasksOfEpic.remove(id);
                if (subtasksOfEpic.isEmpty()) {
                    foundedEpic.setStartTime(LocalDateTime.MAX);
                    foundedEpic.setEndTime(LocalDateTime.MIN);
                } else {
                    if (isEqualSubtasksTimeToEpicsTime(removedSubTask, foundedEpic)) {
                        foundedEpic.subtractionDuration(removedSubTask.getDuration());
                        findNewTimeAndEndTimeOfEpic(foundedEpic);
                    }
                }
                setStatusEpic(foundedEpic);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void findNewTimeAndEndTimeOfEpic(Epic epic) {
        ArrayList<Integer> subTasksOfFoundedEpic = new ArrayList<>(epic.getSubTasks());
        epic.setStartTime(LocalDateTime.MAX);
        epic.setEndTime(LocalDateTime.MIN);
        for (Integer subTaskId : subTasksOfFoundedEpic) {
            SubTask subTask = subTasks.get(subTaskId);
            LocalDateTime startTimeOfEpic = epic.getStartTime();
            LocalDateTime startTimeOfSubtask = subTask.getStartTime();
            LocalDateTime endTimeOfEpic = epic.getEndTime();
            LocalDateTime endTimeOfSubtask = subTask.getEndTime();
            if (startTimeOfEpic.isAfter(startTimeOfSubtask)) {
                epic.setStartTime(startTimeOfSubtask);
            }
            if (endTimeOfEpic.isBefore(endTimeOfSubtask)) {
                epic.setEndTime(endTimeOfSubtask);
            }
        }
    }

    public void setDurationStartTimeAndEndTimeOfEpic(Epic epic, SubTask subTask) {
        epic.increaseDuration(subTask.getDuration());
        LocalDateTime startTimeOfEpic = epic.getStartTime();
        LocalDateTime startTimeOfSubtask = subTask.getStartTime();
        LocalDateTime endTimeOfEpic = epic.getEndTime();
        LocalDateTime endTimeOfSubtask = subTask.getEndTime();
        if (startTimeOfEpic.isAfter(startTimeOfSubtask)) {
            epic.setStartTime(startTimeOfSubtask);
        }
        if (endTimeOfEpic.isBefore(endTimeOfSubtask)) {
            epic.setEndTime(endTimeOfSubtask);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksSortedByStartTime);
    }

    public boolean isTaskOverlapTasksByTime(Task checkedTask, String methodName) {
        for (Task task : tasksSortedByStartTime) {
            if (task.equals(checkedTask)) {
                return false;
            }
            if (isTaskOverlapAnotherTask(checkedTask, task)) {
                throw new TaskOverlapAnotherTaskException("Fail to " + methodName + " because " +
                        checkedTask.getName() + " overlap " + task.getName());
            }
        }
        return false;
    }

    public boolean isTaskOverlapAnotherTask(Task checkedTask, Task anotherTask) {
        LocalDateTime checkedTaskStartTime = checkedTask.getStartTime();
        LocalDateTime checkedTaskEndTime = checkedTask.getEndTime();
        LocalDateTime taskStartTime = anotherTask.getStartTime();
        LocalDateTime taskEndTime = anotherTask.getEndTime();
        return checkedTaskStartTime.isEqual(taskStartTime) && checkedTaskEndTime.isEqual(taskEndTime) ||
                (checkedTaskStartTime.isBefore(taskEndTime)) && (taskStartTime.isBefore(checkedTaskEndTime));
    }

    private boolean isEqualSubtasksTimeToEpicsTime(SubTask subTask, Epic epic) {
        return subTask.getStartTime().isEqual(epic.getStartTime()) ||
                subTask.getEndTime().isEqual(epic.getEndTime());
    }
}