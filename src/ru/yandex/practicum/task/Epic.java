package ru.yandex.practicum.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:03
 */
public class Epic extends Task {

    //Для одного теста пришлось убрать final
    private final ArrayList<Integer> subTasksIds;
    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, 0, LocalDateTime.MAX.toString());
        endTime = LocalDateTime.MIN;
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(String value) {
        super(value);
        String[] task = value.split(",");
        this.endTime = LocalDateTime.parse(task[2]);
        this.subTasksIds = new ArrayList<>();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public List<Integer> getSubTasks() {
        return subTasksIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void increaseDuration(Duration duration) {
        this.duration = this.duration.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return epic.name.equals(name) && epic.description.equals(description);
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
        return id + "," +
                this.startTime.toString() + ","
                + this.endTime.toString() +
                ",EPIC," + name +
                "," + state + "," +
                description;
    }
}