package ru.yandex.practicum.tasks;

import ru.yandex.practicum.States;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<Integer> getSubTasks() {
        return subTasksIds;
    }

    public void setState(States state) {
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
        return Objects.hash(subTasksIds);
    }

    @Override
    public String toString() {
        return id +
                ",EPIC," + name +
                "," + state + "," +
                description;
    }
}