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
    private final List<Integer> subTasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, 0, LocalDateTime.MAX.toString());
        endTime = LocalDateTime.MIN;
        this.subTasksIds = new ArrayList<>();
    }

    public Epic(String value) {
        super(value);
        if (startTime.isEqual(LocalDateTime.MAX)) {
            this.endTime = LocalDateTime.MIN;
        } else {
            this.endTime = this.startTime.plus(duration);
        }
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

    public void subtractionDuration(Duration duration) {
        if(this.duration.minus(duration).isNegative()) {
            this.duration = Duration.ZERO;
        } else {
            this.duration = this.duration.minus(duration);
        }
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
                + duration +
                ",EPIC," + name +
                "," + state + "," +
                description;
    }
}