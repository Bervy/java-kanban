package ru.yandex.practicum.history;

import ru.yandex.practicum.tasks.Task;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Vlad Osipov
 * @create 2022-05-09   19:08
 */
public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_SIZE_OF_BROWSING_HISTORY_TASKS = 10;
    private final List<Task> browsingHistoryTasks = new ArrayList<>(MAX_SIZE_OF_BROWSING_HISTORY_TASKS);


    @Override
    public void add(Task task) {
        if (browsingHistoryTasks.size() == MAX_SIZE_OF_BROWSING_HISTORY_TASKS) {
            browsingHistoryTasks.remove(0);
        }
        browsingHistoryTasks.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return browsingHistoryTasks;
    }
}
