package ru.yandex.practicum.tasks;

import ru.yandex.practicum.States;

import java.util.Objects;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:01
 */
public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected States state;



    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.state = States.NEW;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public States getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return task.id == id && task.name.equals(name) && task.description.equals(description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, state);
    }

    @Override
    public String toString() {
        return "Task {" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", state='" + state + '\'' +
                "} \n";
    }
}