package ru.yandex.practicum.tasks;

import java.util.Objects;

/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:02
 */
public class SubTask extends Task {

    private final Epic epic;

    public SubTask(Epic epic, String name, String description) {
        super(name, description);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epic=" + epic.name +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", state=" + state +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return subTask.id == id;
    }
}