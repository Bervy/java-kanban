package ru.yandex.practicum.task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:03
 */
public class Epic extends Task {

    private final ArrayList<Integer> subTasksIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(String value) {
        super(value);
        this.subTasksIds = new ArrayList<>();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }


    public List<Integer> getSubTasks() {
        return subTasksIds;
    }

    public void setState(State state) {
        super.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return epic.id == id && epic.name.equals(name) && epic.description.equals(description);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode += 31 * hashCode + name.hashCode() + description.hashCode() +
                id + state.hashCode() + subTasksIds.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return id +
                ",EPIC," + name +
                "," + state + "," +
                description;
    }
}