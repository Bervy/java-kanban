package ru.yandex.practicum.tasks;

import java.util.ArrayList;
/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:03
 */
public class Epic extends Task {

    private final ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setState(States state) {
        super.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return epic.id == id;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subTasks=" + subTasks +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", state=" + state +
                '}';
    }
}