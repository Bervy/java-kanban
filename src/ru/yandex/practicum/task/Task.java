package ru.yandex.practicum.task;

import ru.yandex.practicum.exceptions.TaskCreateException;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:01
 */
public class Task {

    protected String name;
    protected String description;
    protected int id;
    protected State state;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        if (name == null || description == null) {
            throw new TaskCreateException("Can't create Task");
        }
        this.name = name;
        this.description = description;
        this.id = 0;
        this.state = State.NEW;
        this.duration = Duration.ofMinutes(0);
        this.startTime = LocalDateTime.MAX;
    }

    public Task(String name, String description, String durationMinutes, String startTime) {
        if (name == null || description == null || durationMinutes == null || startTime == null) {
            throw new TaskCreateException("Can't create Task");
        }
        this.name = name;
        this.description = description;
        this.id = 0;
        this.state = State.NEW;
        this.duration = Duration.parse(durationMinutes);
        this.startTime = LocalDateTime.parse(startTime);
    }

    public Task(String value) {
        String[] task = value.split(",");
        this.id = Integer.parseInt(task[0]);
        this.startTime = LocalDateTime.parse(task[1]);
        this.duration = Duration.parse(task[2]);
        this.name = task[4];
        this.state = State.valueOf(task[5]);
        this.description = task[6];
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

    //Временно, чтобы проверить.
    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getEndTime() {
        if (startTime.isEqual(LocalDateTime.MAX)) {
            return LocalDateTime.MIN;
        } else {
            return startTime.plus(duration);
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        return task.name.equals(name) && task.description.equals(description)
                && task.startTime.isEqual(startTime) && task.duration.equals(duration) && task.state.equals(state);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode += 31 * hashCode + name.hashCode() + description.hashCode() +
                state.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return id + "," +
                startTime.toString() + ","
                + duration +
                ",TASK," + name + "," + state +
                "," + description;
    }
}