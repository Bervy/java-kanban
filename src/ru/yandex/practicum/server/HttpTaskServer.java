package ru.yandex.practicum.server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.adapters.DurationAdapter;
import ru.yandex.practicum.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.exceptions.TaskCreateException;
import ru.yandex.practicum.exceptions.TaskOverlapAnotherTaskException;
import ru.yandex.practicum.service.Managers;
import ru.yandex.practicum.service.TaskManager;
import ru.yandex.practicum.task.Epic;
import ru.yandex.practicum.task.SubTask;
import ru.yandex.practicum.task.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Osipov
 * @create 2022-07-10   12:59
 */
public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder().
            registerTypeAdapter(Duration.class, new DurationAdapter()).
            registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
            setPrettyPrinting().
            create();
    private static final int PORT_HTTP_TASK_MANAGER = 8070;
    private static final String URL = "http://localhost:";
    private static final String KEY = "TEST";
    private final TaskManager httpTaskManager = Managers.getDefault(URL, PORT_HTTP_TASK_MANAGER, KEY);
    HttpServer httpServer;

    public void start() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", this::allTasksHandler);
            httpServer.createContext("/tasks/task", this::taskHandler);
            httpServer.createContext("/tasks/subtask", this::subTaskHandler);
            httpServer.createContext("/tasks/epic", this::epicHandler);
            httpServer.createContext("/tasks/history", this::historyHandler);
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        httpServer.stop(0);
    }

    private void allTasksHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        try (exchange) {
            if ("GET".equals(requestMethod)) {
                responseWithAllTasksOrHistory("PrioritizedTasks", exchange);
            } else {
                wrongMethod(exchange);
            }
        }
    }

    private void taskHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String taskParameters;
        try (exchange) {
            switch (requestMethod) {
                case "GET" -> {
                    taskParameters = exchange.getRequestURI().getQuery();
                    if (taskParameters != null) {
                        int getTaskId = Integer.parseInt(taskParameters.split("=")[1]);
                        Task foundedTask = httpTaskManager.getTask(getTaskId);
                        if (foundedTask != null) {
                            responseTaskFoundWithStatus200(foundedTask, exchange);
                        } else {
                            responseWithStatusCode(404, "No task with this id", exchange);
                        }
                    } else {
                        responseWithAllTasksOrHistory("Tasks", exchange);
                    }
                }
                case "POST" -> {
                    try {
                        Task task = getTaskFromJson("Task", exchange);
                        if (httpTaskManager.addTask(task)) {
                            responseWithStatusCode(201, "Task created", exchange);
                        } else if (httpTaskManager.updateTask(task)) {
                            responseWithStatusCode(200, "Task updated", exchange);
                        } else {
                            responseWithStatusCode(404, "Task already exists", exchange);
                        }
                    } catch (TaskOverlapAnotherTaskException e) {
                        responseWithStatusCode(404, "Task overlap by time", exchange);
                    } catch (TaskCreateException e) {
                        responseWithStatusCode(404, "Task has null fields", exchange);
                    }
                }
                case "DELETE" -> {
                    taskParameters = exchange.getRequestURI().getQuery();
                    if (taskParameters != null) {
                        int deleteId = Integer.parseInt(taskParameters.split("=")[1]);
                        if (httpTaskManager.removeTask(deleteId)) {
                            responseWithStatusCode(200, "Task deleted", exchange);
                        } else {
                            responseWithStatusCode(404, "Task not deleted", exchange);
                        }
                    } else {
                        httpTaskManager.removeAllTasks();
                        int sizeOfTasks = httpTaskManager.getTasks().size();
                        responseWithStatusCode(200, String.valueOf(sizeOfTasks), exchange);
                    }
                }
                default -> wrongMethod(exchange);
            }
        }
    }

    private void subTaskHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String getTaskParameters;
        try (exchange) {
            switch (requestMethod) {
                case "GET" -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        String path = exchange.getRequestURI().getPath();
                        String[] parts = path.split("/");
                        int getId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        if (parts.length == 4 && parts[3].equals("epic")) {
                            Epic foundedEpic = httpTaskManager.getEpic(getId);
                            if (foundedEpic == null) {
                                responseWithStatusCode(404, "Epic not found", exchange);
                            } else {
                                List<Integer> subTasksOfEpic = httpTaskManager.getSubTasksOfEpic(foundedEpic);
                                if (!subTasksOfEpic.isEmpty()) {
                                    String toJson = "{" +
                                            gson.toJson("Subtasks") + " : " +
                                            gson.toJson(subTasksOfEpic) +
                                            "}";
                                    responseWithStatusCode(200, toJson, exchange);
                                } else {
                                    responseWithStatusCode(200, "0", exchange);
                                }
                            }
                        } else {
                            SubTask foundedSubTask = httpTaskManager.getSubTask(getId);
                            if (foundedSubTask != null) {
                                responseTaskFoundWithStatus200(foundedSubTask, exchange);
                            } else {
                                responseWithStatusCode(404, "No subtask with this id", exchange);
                            }
                        }
                    } else {
                        responseWithAllTasksOrHistory("SubTasks", exchange);
                    }
                }
                case "POST" -> {
                    try {
                        SubTask subTask = (SubTask) getTaskFromJson("Subtask", exchange);
                        if (httpTaskManager.addSubTask(subTask)) {
                            responseWithStatusCode(201, "Subtask created", exchange);
                        } else if (httpTaskManager.updateSubTask(subTask)) {
                            responseWithStatusCode(200, "Subtask updated", exchange);
                        } else {
                            responseWithStatusCode(404, "Subtask already exists", exchange);
                        }
                    } catch (TaskOverlapAnotherTaskException e) {
                        responseWithStatusCode(404, "Subtask overlap by time", exchange);
                    } catch (TaskCreateException e) {
                        responseWithStatusCode(404, "Subtask has null fields", exchange);
                    }
                }
                case "DELETE" -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        int deleteId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        if (httpTaskManager.removeSubTask(deleteId)) {
                            responseWithStatusCode(200, "Subtask deleted", exchange);
                        } else {
                            responseWithStatusCode(404, "Subtask not deleted", exchange);
                        }
                    } else {
                        httpTaskManager.removeAllTasks();
                        int sizeOfSubTasks = httpTaskManager.getTasks().size();
                        responseWithStatusCode(200, String.valueOf(sizeOfSubTasks), exchange);
                    }
                }
                default -> wrongMethod(exchange);
            }
        }
    }

    private void epicHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String getTaskParameters;
        try (exchange) {
            switch (requestMethod) {
                case "GET" -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        int getId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        Epic foundedEpic = httpTaskManager.getEpic(getId);
                        if (foundedEpic != null) {
                            responseTaskFoundWithStatus200(foundedEpic, exchange);
                        } else {
                            responseWithStatusCode(404, "No epic with this id", exchange);
                        }
                    } else {
                        responseWithAllTasksOrHistory("Epics", exchange);
                    }
                }
                case "POST" -> {
                    try {
                        Epic epic = (Epic) getTaskFromJson("Epic", exchange);
                        if (httpTaskManager.addEpic(epic)) {
                            responseWithStatusCode(201, "Epic created", exchange);
                        } else if (httpTaskManager.updateEpic(epic)) {
                            responseWithStatusCode(200, "Epic updated", exchange);
                        } else {
                            responseWithStatusCode(404, "Epic already exists", exchange);
                        }
                    } catch (TaskCreateException e) {
                        responseWithStatusCode(404, "Epic has null fields", exchange);
                    }
                }
                case "DELETE" -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        int deleteId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        if (httpTaskManager.removeEpic(deleteId)) {
                            responseWithStatusCode(200, "Epic deleted", exchange);
                        } else {
                            responseWithStatusCode(404, "Epic not deleted", exchange);
                        }
                    } else {
                        httpTaskManager.removeAllEpics();
                        int sizeOfEpics = httpTaskManager.getEpics().size();
                        responseWithStatusCode(200, String.valueOf(sizeOfEpics), exchange);
                    }
                }
                default -> wrongMethod(exchange);
            }
        }
    }

    private Task getTaskFromJson(String type, HttpExchange exchange) throws IOException {
        JsonObject jsonObject = getJsonObject(exchange);
        if (!jsonObject.has("id") ||
                !jsonObject.has("name") ||
                !jsonObject.has("description") ||
                !jsonObject.has("duration") ||
                !jsonObject.has("startTime")) {
            throw new TaskCreateException("Json don't have task fields");
        }
        int id = jsonObject.get("id").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        String duration = jsonObject.get("duration").getAsString();
        String startTime = jsonObject.get("startTime").getAsString();

        switch (type) {
            case "Epic":
                if (!jsonObject.has("endTime") || !jsonObject.has("subTasksIds")) {
                    throw new TaskCreateException("Json don't have epic fields");
                } else {
                    String endTime = jsonObject.get("endTime").getAsString();
                    JsonArray jsonSubTasksIds = jsonObject.get("subTasksIds").getAsJsonArray();
                    Epic epic = new Epic(name, description);
                    List<Integer> subTaskIds = new ArrayList<>();
                    if (jsonSubTasksIds != null) {
                        for (int i = 0; i < jsonSubTasksIds.size(); i++) {
                            subTaskIds.add(jsonSubTasksIds.get(i).getAsInt());
                        }
                    }
                    epic.setId(id);
                    epic.setStartTime(LocalDateTime.parse(startTime));
                    epic.setEndTime(LocalDateTime.parse(endTime));
                    epic.setDuration(Duration.parse(duration));
                    epic.setSubTasksIds(subTaskIds);
                    return epic;
                }
            case "Subtask":
                if (!jsonObject.has("epicId")) {
                    throw new TaskCreateException("Json don't have subtask fields");
                } else {
                    int epicId = jsonObject.get("epicId").getAsInt();
                    SubTask subTask = new SubTask(epicId, name, description, duration, startTime);
                    subTask.setId(id);
                    return subTask;
                }
            case "Task":
                Task task = new Task(name, description, duration, startTime);
                task.setId(id);
                return task;
            default:
                System.out.println("Wrong type");
                return null;
        }
    }

    private void historyHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        try (exchange) {
            if (requestMethod.equals("GET")) {
                responseWithAllTasksOrHistory("History", exchange);
            } else {
                wrongMethod(exchange);
            }
        }
    }

    private void responseWithAllTasksOrHistory(String type, HttpExchange exchange) throws IOException {
        switch (type) {
            case "Tasks" -> {
                List<Task> tasks = httpTaskManager.getTasks();
                listToJson(tasks, type, exchange);
            }
            case "SubTasks" -> {
                List<SubTask> subTasks = httpTaskManager.getSubTasks();
                listToJson(subTasks, type, exchange);
            }
            case "Epics" -> {
                List<Epic> epics = httpTaskManager.getEpics();
                listToJson(epics, type, exchange);
            }
            case "History" -> {
                List<Task> history = httpTaskManager.getHistory();
                listToJson(history, type, exchange);
            }
            case "PrioritizedTasks" -> {
                List<Task> prioritizedTasks = httpTaskManager.getPrioritizedTasks();
                listToJson(prioritizedTasks, type, exchange);
            }
            default -> System.out.println("Wrong type");
        }
    }

    private <T> void listToJson(List<T> list, String type, HttpExchange exchange) throws IOException {
        if (!list.isEmpty()) {
            String toJson = "{" +
                    gson.toJson(type) + " : " +
                    gson.toJson(list) +
                    "}";
            responseWithStatusCode(200, toJson, exchange);
        } else {
            responseWithStatusCode(200, gson.toJson(0), exchange);
        }
    }

    private <T> void responseTaskFoundWithStatus200(T foundedTask, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, 0);
        String toJson = gson.toJson(foundedTask);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(toJson.getBytes());
        }
    }

    private void responseWithStatusCode(int statusCode, String response, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(statusCode, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void wrongMethod(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        String answer = "Wrong method";
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(answer.getBytes());
        }
        exchange.close();
    }

    private JsonObject getJsonObject(HttpExchange exchange) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
             BufferedReader br = new BufferedReader(isr)) {
            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }
            JsonElement jsonElement = JsonParser.parseString(buf.toString());
            return jsonElement.getAsJsonObject();
        }
    }
}