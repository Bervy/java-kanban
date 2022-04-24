package ru.yandex.practicum.controller;

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
public class Manager {

    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, SubTask> subTasks;
    private Integer taskId;
    private Integer epicId;
    private Integer subTaskId;

    public Manager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.taskId = 0;
        this.epicId = 0;
        this.subTaskId = 0;
    }

    public List<Task> getListOfTasks () {
        return new ArrayList<>(tasks.values());
    }

    public void removeAllTasks () {
        tasks.clear();
        taskId = 0;
    }

    public Task getTaskById (int taskId) {
        return tasks.getOrDefault(taskId, null);
    }

    public boolean addTask (Task newTask) {
        if (tasks.containsValue(newTask)) {
            return false;
        } else {
            tasks.put(taskId, newTask);
            newTask.setId(taskId);
            taskId++;
            return true;
        }
    }

    public boolean updateTask (Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeTaskById (int taskId) {
        return tasks.remove(taskId) != null;
    }

    public List<Epic> getListOfEpics () {
        return new ArrayList<>(epics.values());
    }

    public void removeAllEpics () {
        epics.clear();
        subTasks.clear();
        epicId = 0;
        subTaskId = 0;
    }

    public Epic getEpicById (int epicId) {
        return epics.getOrDefault(epicId, null);
    }

    public boolean addEpic (Epic newEpic) {
        if (epics.containsValue(newEpic)) {
            return false;
        } else {
            epics.put(epicId, newEpic);
            newEpic.setId(epicId);
            epicId++;
            return true;
        }
    }

    public boolean updateEpic (Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            epics.put(updatedEpic.getId(), updatedEpic);
            return true;
        } else {
            return false;
        }
    }

    public boolean removeEpicById (int epicId) {
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
            if(s.getState().equals("IN_PROGRESS")) {
                count++;
            } else if (s.getState().equals("DONE")) {
                count +=2;
            }
        }
        if (count == 0) {
            epic.setState("NEW");
        } else if (count == subTasksOfFoundedEpic.size() * 2) {
            epic.setState("DONE");
        } else {
            epic.setState("IN_PROGRESS");
        }
    }

    public ArrayList<SubTask> getSubTasksOfEpic (Epic epic) {
        if(epics.containsKey(epic.getId())) {
            return epic.getSubTasks();
        } else {
            return null;
        }
    }

    public List<SubTask> getListOfSubTasks () {
        return new ArrayList<>(subTasks.values());
    }

    public void removeAllSubTasks () {
        subTasks.clear();
        for(Epic epic : epics.values()) {
            epic.getSubTasks().clear();
        }
        subTaskId = 0;
    }

    public SubTask getSubTaskById (int subTaskId) {
        return subTasks.getOrDefault(subTaskId, null);
    }

    public boolean addSubTask (Epic epic, SubTask newSubtask) {
        if(subTasks.containsValue(newSubtask)) {
            return false;
        } else {
            subTasks.put(subTaskId, newSubtask);
            newSubtask.setId(subTaskId);
            subTaskId++;
            epic.getSubTasks().add(newSubtask);
            return true;
        }
    }

    public boolean updateSubTask (SubTask updatedSubTask) {
        if(subTasks.containsKey(updatedSubTask.getId())) {
            subTasks.put(updatedSubTask.getId(), updatedSubTask);
            Epic foundedEpic = getEpicBySubTask(updatedSubTask);
            if (foundedEpic != null) {
                for(SubTask subTask : foundedEpic.getSubTasks()) {
                    if (Objects.equals(subTask.getId(), updatedSubTask.getId())) {
                        foundedEpic.getSubTasks().set(updatedSubTask.getId(), updatedSubTask);
                        setStatusEpic(foundedEpic);
                        return true;
                    }
                }
            } else {
                return false;
            }
        }
        return false;
    }

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
}