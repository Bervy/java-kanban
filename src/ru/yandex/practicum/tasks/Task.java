package ru.yandex.practicum.tasks;
/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:01
 */
public class Task {

    private final String name;
    private final String description;
    private int id;
    protected String state;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.state = "NEW";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", state='" + state + '\'' +
                '}';
    }
}