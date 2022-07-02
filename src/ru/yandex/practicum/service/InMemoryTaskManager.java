package ru.yandex.practicum.service;

import ru.yandex.practicum.task.State;
import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.task.Epic;
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
    private final TreeSet<Task> tasksSortedByStartTime = new TreeSet<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer generatorId;


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.generatorId = 0;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
        updateTasksSortedByStartTime();
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
        generatorId++;
        if (tasks.containsValue(newTask) && !isTaskOverlapTasksByTime(newTask)) {
            generatorId--;
        } else {
                tasks.put(generatorId, newTask);
                newTask.setId(generatorId);
                tasksSortedByStartTime.add(newTask);
        }
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (updatedTask != null && tasks.containsKey(updatedTask.getId()) &&
                isTaskOverlapTasksByTime(updatedTask)) {
            tasks.put(updatedTask.getId(), updatedTask);
            updateTasksSortedByStartTime();
        }
    }

    @Override
    public void removeTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
            updateTasksSortedByStartTime();
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
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
        subTasks.clear();
        updateTasksSortedByStartTime();
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
        generatorId++;
        if (epics.containsValue(newEpic)) {
            generatorId--;
        } else {
            epics.put(generatorId, newEpic);
            newEpic.setId(generatorId);
        }
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
            for (Integer subTaskId : epics.get(id).getSubTasks()) {
                for (SubTask subTask1 : copySubTasks.values()) {
                    if (subTaskId == subTask1.getId()) {
                        historyManager.remove(subTask1.getId());
                        subTasks.remove(subTask1.getId());
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
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epic.setDuration(0);
        }
        updateTasksSortedByStartTime();
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
        generatorId++;
        if (subTasks.containsValue(newSubtask) || isTaskOverlapTasksByTime(newSubtask)) {
            generatorId--;
        } else {
                subTasks.put(generatorId, newSubtask);
                newSubtask.setId(generatorId);
                tasksSortedByStartTime.add(newSubtask);
                Epic foundedEpic = epics.get(newSubtask.getEpicId());
                foundedEpic.getSubTasks().add(newSubtask.getId());
                setDurationAndStartTimeOfEpic(foundedEpic);
                updateTasksSortedByStartTime();
        }
    }

    @Override
    public void updateSubTask(SubTask updatedSubTask) {
        if (updatedSubTask != null && subTasks.containsKey(updatedSubTask.getId()) &&
                !isTaskOverlapTasksByTime(updatedSubTask)) {
            Epic foundedEpic = getEpicBySubTask(updatedSubTask);
            if (foundedEpic != null) {
                subTasks.put(updatedSubTask.getId(), updatedSubTask);
                setDurationAndStartTimeOfEpic(foundedEpic);
                setStatusEpic(foundedEpic);
                updateTasksSortedByStartTime();
            }
        }
    }

    @Override
    public void removeSubTask(Integer id) {
        SubTask removedSubTask = subTasks.remove(id);
        if (removedSubTask != null) {
            historyManager.remove(id);
            Epic foundedEpic = getEpicBySubTask(removedSubTask);
            if (foundedEpic != null) {
                List<Integer> subtasksOfEpic = foundedEpic.getSubTasks();
                subtasksOfEpic.remove(id);
                if(subtasksOfEpic.isEmpty()) {
                    foundedEpic.setStartTime(LocalDateTime.MAX);
                    foundedEpic.setEndTime(LocalDateTime.MIN);
                } else {
                    SubTask firstSubtaskOfEpic = subTasks.get(subtasksOfEpic.get(0));
                    foundedEpic.setStartTime(firstSubtaskOfEpic.getStartTime());
                    foundedEpic.setEndTime(firstSubtaskOfEpic.getEndTime());
                    setDurationAndStartTimeOfEpic(foundedEpic);
                    setStatusEpic(foundedEpic);
                }
            }
            updateTasksSortedByStartTime();
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

    public void setDurationAndStartTimeOfEpic(Epic epic) {
        ArrayList<Integer> subTasksOfFoundedEpic = new ArrayList<>(epic.getSubTasks());
        epic.setDuration(0);

        for (Integer subTaskId : subTasksOfFoundedEpic) {
            SubTask subTask = subTasks.get(subTaskId);
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
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksSortedByStartTime);
    }

    private void updateTasksSortedByStartTime() {
        tasksSortedByStartTime.clear();
        tasksSortedByStartTime.addAll(tasks.values());
        tasksSortedByStartTime.addAll(subTasks.values());
    }

    public boolean isTaskOverlapTasksByTime(Task checkedTask) {
        for (Task task : tasksSortedByStartTime) {
            if(task.equals(checkedTask)) {
                return false;
            }
            if(isTaskOverlapAnotherTask(checkedTask, task)) {
                return true;
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
}