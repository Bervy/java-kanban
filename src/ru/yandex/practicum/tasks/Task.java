package ru.yandex.practicum.tasks;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:01
 */
public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected States state;

    public enum States {
        NEW,
        IN_PROGRESS,
        DONE
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = 0;
        this.state = States.NEW;
    }
    //Временный метод для проверки состояний задачи
    public void changeState () {
        if (state.equals(States.NEW)) {
            state = States.IN_PROGRESS;
        } else if (state.equals(States.IN_PROGRESS)) {
            state = States.DONE;
        }
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

    public void setState(States state) {
        this.state = state;
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
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return task.id == id && task.name.equals(name) && task.description.equals(description);
    }
}