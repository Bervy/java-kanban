package ru.yandex.practicum.tasks;
/**
 * @author Vlad Osipov
 * @create 2022-04-23   10:02
 */
public class SubTask extends Task {

    Epic epic;

    public SubTask(Epic epic, String name, String description) {
        super(name, description);
        this.epic = epic;

    }

    public Epic getEpic() {
        return epic;
    }
}