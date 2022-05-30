package ru.yandex.practicum.service;

import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.States;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

import java.util.*;

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
    public Task getTaskById (int id) {
        Task foundedTask  = tasks.get(id);
        if(foundedTask != null) {
            historyManager.add(tasks.get(id));
            return foundedTask;
        } else {
            return null;
        }
    }

    @Override
    public void addTask (Task newTask) {
        generatorId++;
        if (tasks.containsValue(newTask)) {
            generatorId--;
        } else {
            tasks.put(generatorId, newTask);
            newTask.setId(generatorId);
        }
    }

    @Override
    public void updateTask (Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    @Override
    public void removeTaskById (int id) {
        if(tasks.containsKey(id)) {
            historyManager.remove(id);
            tasks.remove(id);
        }
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
    public Epic getEpicById (int id) {
        Epic foundedEpic = epics.get(id);
        if(foundedEpic != null) {
            historyManager.add(epics.get(id));
            return foundedEpic;
        } else {
            return null;
        }
    }

    @Override
    public void addEpic (Epic newEpic) {
        generatorId++;
        if (epics.containsValue(newEpic)) {
            generatorId--;
        } else {
            epics.put(generatorId, newEpic);
            newEpic.setId(generatorId);
        }
    }

    @Override
    public void updateEpic (Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
        }
    }

    @Override
    public void removeEpicById (int id) {
        HashMap<Integer, SubTask> copySubTasks = new HashMap<>(subTasks);
        if(epics.get(id) != null) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                for (SubTask subTask1 : copySubTasks.values()) {
                    if (subTask.equals(subTask1)) {
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
    public List<SubTask> getSubTasksOfEpic (Epic epic) {
        if(epics.containsKey(epic.getId())) {
            return epic.getSubTasks();
        } else {
            return Collections.emptyList();
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
    public SubTask getSubTaskById (int id) {
        SubTask foundedSubTask = subTasks.get(id);
        if(foundedSubTask != null) {
            historyManager.add(subTasks.get(id));
            return foundedSubTask;
        } else {
            return null;
        }
    }

    @Override
    public void addSubTask (SubTask newSubtask) {
        generatorId++;
        if(subTasks.containsValue(newSubtask)) {
            generatorId--;
        } else {
            subTasks.put(generatorId, newSubtask);
            newSubtask.setId(generatorId);
            newSubtask.getEpic().getSubTasks().add(newSubtask);
        }
    }

    @Override
    public void updateSubTask (SubTask updatedSubTask) {
        if(subTasks.containsKey(updatedSubTask.getId())) {
            subTasks.put(updatedSubTask.getId(), updatedSubTask);
            setStatusEpic(Objects.requireNonNull(getEpicBySubTask(updatedSubTask)));
        }
    }

    @Override
    public void removeSubTaskById (int id) {
        SubTask removedSubTask = subTasks.remove(id);
        if(removedSubTask != null) {
            historyManager.remove(id);
            Epic foundedEpic = getEpicBySubTask(removedSubTask);
            if (foundedEpic != null) {
                foundedEpic.getSubTasks().remove(removedSubTask);
                setStatusEpic(foundedEpic);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
       return historyManager.getHistory();
    }
}