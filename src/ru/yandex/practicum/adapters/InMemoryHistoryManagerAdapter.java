package ru.yandex.practicum.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import ru.yandex.practicum.history.HistoryManager;
import ru.yandex.practicum.history.InMemoryHistoryManager;
import ru.yandex.practicum.service.HTTPTaskManager;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.IOException;
import java.util.List;

/**
 * @author Vlad Osipov
 * @create 2022-07-11   19:38
 */
public class InMemoryHistoryManagerAdapter extends TypeAdapter<InMemoryHistoryManager> {

    HTTPTaskManager httpTaskManager;

    public InMemoryHistoryManagerAdapter(HTTPTaskManager httpTaskManager) {
        this.httpTaskManager = httpTaskManager;
    }

    public InMemoryHistoryManager read(JsonReader reader) throws IOException {
        HistoryManager historyManager = Managers.getDefaultHistory();
        List<Task> tasks = httpTaskManager.getTasks();
        List<Epic> epics = httpTaskManager.getEpics();
        List<SubTask> subTasks = httpTaskManager.getSubTasks();

        String jsonHistory = reader.nextString();
        String[] parts = jsonHistory.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (tasks.get(i) != null) {
                historyManager.add(tasks.get(i));
            }
            if (epics.get(i) != null) {
                historyManager.add(epics.get(i));
            }
            if (subTasks.get(i) != null) {
                historyManager.add(subTasks.get(i));
            }
        }
        return (InMemoryHistoryManager) historyManager;
    }

    public void write(JsonWriter writer, InMemoryHistoryManager value) throws IOException {
        StringBuilder result = new StringBuilder();
        List<Task> historyTasks = httpTaskManager.getHistory();
        for (Task task : historyTasks) {
            result.append(task.getId()).append(",");
        }
        writer.value(result.toString());
    }
}