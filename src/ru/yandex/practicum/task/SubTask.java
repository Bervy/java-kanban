package ru.yandex.practicum.task;

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

    public SubTask(Integer epicId, String name, String description, String durationMinutes, String startTime) {
        super(name, description, durationMinutes, startTime);
        this.epicId = epicId;
    }

    public SubTask(String value) {
        super(value);
        String[] subTask = value.split(",");
        this.epicId = Integer.parseInt(subTask[7]);

    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }


    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubTask)) return false;
        SubTask subTask = (SubTask) o;
        return subTask.name.equals(name) && subTask.description.equals(description)
                && subTask.startTime.isEqual(startTime) && subTask.duration.equals(duration) && subTask.state.equals(state);
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        hashCode += 31 * hashCode + name.hashCode() + description.hashCode() +
                id + state.hashCode() + epicId;
        return hashCode;
    }

    @Override
    public String toString() {
        return id + "," +
                startTime.toString() + ","
                + duration +
                ",SUBTASK," + name +
                "," + state + "," +
                description + "," + epicId;
    }
}