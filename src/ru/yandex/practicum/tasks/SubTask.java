package ru.yandex.practicum.tasks;

import java.util.Objects;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:02
 */
public class SubTask extends Task {

    private final Integer epicId;

    public SubTask(Integer epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String value) {
        super(value);
        String[] subTask = value.split(",");
        this.epicId = Integer.parseInt(subTask[5]);

    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return subTask.id == id && subTask.name.equals(name) && subTask.description.equals(description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicId);
    }

    @Override
    public String toString() {
        return id + ",SUBTASK," + name + "," + state + "," + description + "," + epicId;
    }
}