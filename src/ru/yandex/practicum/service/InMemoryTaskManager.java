package ru.yandex.practicum.service;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.States;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:00
 */
public class InMemoryTaskManager implements TaskManager {

    private Integer generatorId;
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.generatorId = 0;
    }

    @Override
    public List<Task> getListOfTasks () {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks () {
        tasks.clear();
    }

    @Override
    public Task getTaskById (int taskId) {
        Task foundedTask  = tasks.get(taskId);
        if(foundedTask != null) {
            historyManager.add(tasks.get(taskId));
            return foundedTask;
        } else {
            return null;
        }
    }

    @Override
    public boolean addTask (Task newTask) {
        generatorId++;
        if (tasks.containsValue(newTask)) {
            generatorId--;
            return false;
        } else {
            tasks.put(generatorId, newTask);
            newTask.setId(generatorId);
            return true;
        }
    }

    @Override
    public boolean updateTask (Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeTaskById (int taskId) {
        return tasks.remove(taskId) != null;
    }

    @Override
    public List<Epic> getListOfEpics () {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics () {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpicById (int epicId) {
        Epic foundedEpic = epics.get(epicId);
        if(foundedEpic != null) {
            historyManager.add(epics.get(epicId));
            return foundedEpic;
        } else {
            return null;
        }
    }

    @Override
    public boolean addEpic (Epic newEpic) {
        generatorId++;
        if (epics.containsValue(newEpic)) {
            generatorId--;
            return false;
        } else {
            epics.put(generatorId, newEpic);
            newEpic.setId(generatorId);
            return true;
        }
    }

    @Override
    public boolean updateEpic (Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean removeEpicById (int epicId) {
        HashMap<Integer, SubTask> copySubTasks = new HashMap<>(subTasks);
        if(epics.get(epicId) == null) {
            return false;
        }
        for(SubTask subTask : epics.get(epicId).getSubTasks()) {
            for(SubTask subTask1 : copySubTasks.values()) {
                if(subTask.equals(subTask1)) {
                    subTasks.remove(subTask1.getId());
                }
            }
        }
        return epics.remove(epicId) != null;
    }

    /**
     * @param subTask Подзадача по которой надо найти эпик
     * @return Возвращает эпик, найденный по подзадаче
     */
    private Epic getEpicBySubTask(SubTask subTask) {
        for (Epic epic : epics.values()) {
            if(epic.getSubTasks().contains(subTask)) {
                return epic;
            }
        }
        return null;
    }
    /**
     * @param epic Эпик, для которого надо рассчитать его статус
     * Метод рассчитывает статус для переданного через параметры эпика
     */
    private void setStatusEpic(Epic epic) {
        ArrayList<SubTask> subTasksOfFoundedEpic = new ArrayList<>(epic.getSubTasks());
        int count = 0;

        for (SubTask s : subTasksOfFoundedEpic) {
            if(s.getState() == States.IN_PROGRESS) {
                count++;
            } else if (s.getState() == States.DONE) {
                count +=2;
            }
        }
        if (count == 0) {
            epic.setState(States.NEW);
        } else if (count == subTasksOfFoundedEpic.size() * 2) {
            epic.setState(States.DONE);
        } else {
            epic.setState(States.IN_PROGRESS);
        }
    }

    @Override
    public ArrayList<SubTask> getSubTasksOfEpic (Epic epic) {
        if(epics.containsKey(epic.getId())) {
            return epic.getSubTasks();
        } else {
            return null;
        }
    }

    @Override
    public List<SubTask> getListOfSubTasks () {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllSubTasks () {
        subTasks.clear();
        for(Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
    }

    @Override
    public SubTask getSubTaskById (int subTaskId) {
        SubTask foundedSubTask = subTasks.get(subTaskId);
        if(foundedSubTask != null) {
            historyManager.add(subTasks.get(subTaskId));
            return foundedSubTask;
        } else {
            return null;
        }
    }

    @Override
    public boolean addSubTask (SubTask newSubtask) {
        generatorId++;
        if(subTasks.containsValue(newSubtask)) {
            generatorId--;
            return false;
        } else {
            subTasks.put(generatorId, newSubtask);
            newSubtask.setId(generatorId);
            newSubtask.getEpic().getSubTasks().add(newSubtask);
            return true;
        }
    }

    @Override
    public boolean updateSubTask (SubTask updatedSubTask) {
        if(subTasks.containsKey(updatedSubTask.getId())) {
            subTasks.put(updatedSubTask.getId(), updatedSubTask);
            setStatusEpic(Objects.requireNonNull(getEpicBySubTask(updatedSubTask)));
        }
        return false;
    }

    @Override
    public boolean removeSubTaskById (int subTaskId) {
        SubTask removedSubTask = subTasks.remove(subTaskId);
        if(removedSubTask != null) {
            Epic foundedEpic = getEpicBySubTask(removedSubTask);
            if (foundedEpic != null) {
                foundedEpic.getSubTasks().remove(removedSubTask);
                setStatusEpic(foundedEpic);
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
       return historyManager.getHistory();
    }
}