package ru.yandex.practicum.task;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:01
 */
public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected State state;


    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.state = State.NEW;
    }

    public Task(String value) {
        String[] task = value.split(",");
        this.id = Integer.parseInt(task[0]);
        this.name = task[2];
        this.state = State.valueOf(task[3]);
        this.description = task[4];
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
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
        int hashCode = super.hashCode();
        hashCode += 31 * hashCode + name.hashCode() + description.hashCode() +
                id + state.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return id + ",TASK," + name + "," + state + "," + description;
    }
}