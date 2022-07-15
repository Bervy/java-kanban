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
import ru.yandex.practicum.task.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static ru.yandex.practicum.server.KVServer.CREATED;
import static ru.yandex.practicum.server.KVServer.NOT_FOUND;
import static ru.yandex.practicum.server.KVServer.*;
import static ru.yandex.practicum.server.TaskResponseState.*;
import static ru.yandex.practicum.task.TaskCollectionType.*;
import static ru.yandex.practicum.task.TaskType.*;

/**
 * @author Vlad Osipov
 * @create 2022-07-10   12:59
 */
public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final Gson GSON = new GsonBuilder().
            registerTypeAdapter(Duration.class, new DurationAdapter()).
            registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).
            setPrettyPrinting().
            create();
    private static final int PORT_HTTP_TASK_MANAGER = 8070;
    private static final String URL = "http://localhost:";
    private static final String KEY = "TEST";
    private static final String WRONG_METHOD = "Wrong method";
    private static final String WRONG_TYPE = "Wrong type";
    private static final String TASKS_PATH = "/tasks";
    private final TaskManager httpTaskManager = Managers.getDefault(URL, PORT_HTTP_TASK_MANAGER, KEY);
    private HttpServer httpServer;

    public void start() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext(TASKS_PATH, this::allTasksHandler);
            httpServer.createContext(TASKS_PATH + "/task", this::taskHandler);
            httpServer.createContext(TASKS_PATH + "/subtask", this::subTaskHandler);
            httpServer.createContext(TASKS_PATH + "/epic", this::epicHandler);
            httpServer.createContext(TASKS_PATH + "/history", this::historyHandler);
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
            if (GET.equals(requestMethod)) {
                responseWithAllTasksOrHistory(PRIORITIZED_TASKS, exchange);
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
                case GET -> {
                    taskParameters = exchange.getRequestURI().getQuery();
                    if (taskParameters != null) {
                        int getTaskId = Integer.parseInt(taskParameters.split("=")[1]);
                        Task foundedTask = httpTaskManager.getTask(getTaskId);
                        if (foundedTask != null) {
                            responseTaskFoundWithSuccessStatus(foundedTask, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    TASK.name() +
                                            " " +
                                            TaskResponseState.NOT_FOUND, exchange);
                        }
                    } else {
                        responseWithAllTasksOrHistory(TASKS, exchange);
                    }
                }
                case POST -> {
                    try {
                        Task task = getTaskFromJson(TASK, exchange);
                        if (httpTaskManager.isAddTask(task)) {
                            responseWithStatusCode(CREATED, TASK.name() + " " +
                                    TaskResponseState.CREATED, exchange);
                        } else if (httpTaskManager.isUpdateTask(task)) {
                            responseWithStatusCode(OK,
                                    TASK.name() +
                                            " " +
                                            UPDATED, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    TASK.name() +
                                            " " +
                                            ALREADY_EXISTS, exchange);
                        }
                    } catch (TaskOverlapAnotherTaskException e) {
                        responseWithStatusCode(NOT_FOUND,
                                TASK.name() +
                                        " " +
                                        OVERLAP_BY_TIME, exchange);
                    } catch (TaskCreateException e) {
                        responseWithStatusCode(NOT_FOUND,
                                TASK.name() +
                                        " " +
                                        HAS_NULL_FIELDS, exchange);
                    }
                }
                case DELETE -> {
                    taskParameters = exchange.getRequestURI().getQuery();
                    if (taskParameters != null) {
                        int deleteId = Integer.parseInt(taskParameters.split("=")[1]);
                        if (httpTaskManager.isRemoveTask(deleteId)) {
                            responseWithStatusCode(OK,
                                    TASK.name() +
                                            " " +
                                            DELETED, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    TASK.name() +
                                            " " +
                                            NOT_DELETED, exchange);
                        }
                    } else {
                        httpTaskManager.removeAllTasks();
                        int sizeOfTasks = httpTaskManager.getTasks().size();
                        responseWithStatusCode(OK, String.valueOf(sizeOfTasks), exchange);
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
                case GET -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        String path = exchange.getRequestURI().getPath();
                        String[] parts = path.split("/");
                        int getId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        if (parts.length == 4 && parts[3].equals("epic")) {
                            Epic foundedEpic = httpTaskManager.getEpic(getId);
                            if (foundedEpic == null) {
                                responseWithStatusCode(NOT_FOUND,
                                        EPIC.name() +
                                                " " +
                                                TaskResponseState.NOT_FOUND, exchange);
                            } else {
                                List<Integer> subTasksOfEpic = httpTaskManager.getSubTasksOfEpic(foundedEpic);
                                if (!subTasksOfEpic.isEmpty()) {
                                    String toJson = "{" +
                                            GSON.toJson(SUBTASKS) + " : " +
                                            GSON.toJson(subTasksOfEpic) +
                                            "}";
                                    responseWithStatusCode(OK, toJson, exchange);
                                } else {
                                    responseWithStatusCode(OK, "0", exchange);
                                }
                            }
                        } else {
                            SubTask foundedSubTask = httpTaskManager.getSubTask(getId);
                            if (foundedSubTask != null) {
                                responseTaskFoundWithSuccessStatus(foundedSubTask, exchange);
                            } else {
                                responseWithStatusCode(NOT_FOUND,
                                        SUBTASK.name() +
                                                " " +
                                                TaskResponseState.NOT_FOUND, exchange);
                            }
                        }
                    } else {
                        responseWithAllTasksOrHistory(SUBTASKS, exchange);
                    }
                }
                case POST -> {
                    try {
                        SubTask subTask = (SubTask) getTaskFromJson(SUBTASK, exchange);
                        if (httpTaskManager.isAddSubTask(subTask)) {
                            responseWithStatusCode(CREATED,
                                    SUBTASK.name() +
                                            " " +
                                            TaskResponseState.CREATED, exchange);
                        } else if (httpTaskManager.isUpdateSubTask(subTask)) {
                            responseWithStatusCode(OK,
                                    SUBTASK.name() +
                                            " " +
                                            UPDATED, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    SUBTASK.name() +
                                            " " +
                                            ALREADY_EXISTS, exchange);
                        }
                    } catch (TaskOverlapAnotherTaskException e) {
                        responseWithStatusCode(NOT_FOUND,
                                SUBTASK.name() +
                                        " " +
                                        OVERLAP_BY_TIME, exchange);
                    } catch (TaskCreateException e) {
                        responseWithStatusCode(NOT_FOUND,
                                SUBTASK.name() +
                                        " " +
                                        HAS_NULL_FIELDS, exchange);
                    }
                }
                case DELETE -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        int deleteId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        if (httpTaskManager.isRemoveSubTask(deleteId)) {
                            responseWithStatusCode(OK,
                                    SUBTASK.name() +
                                            " " +
                                            DELETED, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    SUBTASK.name() +
                                            " " +
                                            NOT_DELETED, exchange);
                        }
                    } else {
                        httpTaskManager.removeAllTasks();
                        int sizeOfSubTasks = httpTaskManager.getTasks().size();
                        responseWithStatusCode(OK, String.valueOf(sizeOfSubTasks), exchange);
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
                case GET -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        int getId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        Epic foundedEpic = httpTaskManager.getEpic(getId);
                        if (foundedEpic != null) {
                            responseTaskFoundWithSuccessStatus(foundedEpic, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    EPIC.name() +
                                            " " +
                                            TaskResponseState.NOT_FOUND, exchange);
                        }
                    } else {
                        responseWithAllTasksOrHistory(EPICS, exchange);
                    }
                }
                case POST -> {
                    try {
                        Epic epic = (Epic) getTaskFromJson(EPIC, exchange);
                        if (httpTaskManager.isAddEpic(epic)) {
                            responseWithStatusCode(CREATED,
                                    EPIC.name() +
                                            " " +
                                            TaskResponseState.CREATED, exchange);
                        } else if (httpTaskManager.isUpdateEpic(epic)) {
                            responseWithStatusCode(OK,
                                    EPIC.name() +
                                            " " +
                                            UPDATED, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    EPIC.name() +
                                            " " +
                                            ALREADY_EXISTS, exchange);
                        }
                    } catch (TaskCreateException e) {
                        responseWithStatusCode(NOT_FOUND,
                                EPIC.name() +
                                        " " +
                                        HAS_NULL_FIELDS, exchange);
                    }
                }
                case DELETE -> {
                    getTaskParameters = exchange.getRequestURI().getQuery();
                    if (getTaskParameters != null) {
                        int deleteId = Integer.parseInt(getTaskParameters.split("=")[1]);
                        if (httpTaskManager.isRemoveEpic(deleteId)) {
                            responseWithStatusCode(OK,
                                    EPIC.name() +
                                            " " +
                                            DELETED, exchange);
                        } else {
                            responseWithStatusCode(NOT_FOUND,
                                    EPIC.name() +
                                            " " +
                                            NOT_DELETED, exchange);
                        }
                    } else {
                        httpTaskManager.removeAllEpics();
                        int sizeOfEpics = httpTaskManager.getEpics().size();
                        responseWithStatusCode(OK, String.valueOf(sizeOfEpics), exchange);
                    }
                }
                default -> wrongMethod(exchange);
            }
        }
    }

    private boolean isJsonObjectTask(JsonObject jsonObject) {
        return !jsonObject.has("id") ||
                !jsonObject.has("name") ||
                !jsonObject.has("description") ||
                !jsonObject.has("duration") ||
                !jsonObject.has("startTime");
    }

    private boolean isJsonObjectEpic(JsonObject jsonObject) {
        return !jsonObject.has("endTime") ||
                !jsonObject.has("subTasksIds");
    }

    private boolean isJsonObjectSubTask(JsonObject jsonObject) {
        return !jsonObject.has("epicId");
    }

    private Task getTaskFromJson(TaskType type, HttpExchange exchange) throws IOException {
        JsonObject jsonObject = getJsonObject(exchange);
        if (isJsonObjectTask(jsonObject)) {
            throw new TaskCreateException("Json don't have task fields");
        }
        int id = jsonObject.get("id").getAsInt();
        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();
        String duration = jsonObject.get("duration").getAsString();
        String startTime = jsonObject.get("startTime").getAsString();

        switch (type) {
            case EPIC:
                if (isJsonObjectEpic(jsonObject)) {
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
            case SUBTASK:
                if (isJsonObjectSubTask(jsonObject)) {
                    throw new TaskCreateException("Json don't have subtask fields");
                } else {
                    int epicId = jsonObject.get("epicId").getAsInt();
                    SubTask subTask = new SubTask(epicId, name, description, duration, startTime);
                    subTask.setId(id);
                    return subTask;
                }
            case TASK:
                Task task = new Task(name, description, duration, startTime);
                task.setId(id);
                return task;
            default:
                System.out.println(WRONG_TYPE);
                return null;
        }
    }

    private void historyHandler(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        try (exchange) {
            if (requestMethod.equals(KVServer.GET)) {
                responseWithAllTasksOrHistory(HISTORY_TASKS, exchange);
            } else {
                wrongMethod(exchange);
            }
        }
    }

    private void responseWithAllTasksOrHistory(TaskCollectionType type, HttpExchange exchange) throws IOException {
        switch (type) {
            case TASKS -> {
                List<Task> tasks = httpTaskManager.getTasks();
                listToJson(tasks, type, exchange);
            }
            case SUBTASKS -> {
                List<SubTask> subTasks = httpTaskManager.getSubTasks();
                listToJson(subTasks, type, exchange);
            }
            case EPICS -> {
                List<Epic> epics = httpTaskManager.getEpics();
                listToJson(epics, type, exchange);
            }
            case HISTORY_TASKS -> {
                List<Task> history = httpTaskManager.getHistory();
                listToJson(history, type, exchange);
            }
            case PRIORITIZED_TASKS -> {
                List<Task> prioritizedTasks = httpTaskManager.getPrioritizedTasks();
                listToJson(prioritizedTasks, type, exchange);
            }
            default -> System.out.println(WRONG_TYPE);
        }
    }

    private <T> void listToJson(List<T> list, TaskCollectionType type, HttpExchange exchange) throws IOException {
        if (!list.isEmpty()) {
            String toJson = "{" +
                    GSON.toJson(type) + " : " +
                    GSON.toJson(list) +
                    "}";
            responseWithStatusCode(OK, toJson, exchange);
        } else {
            responseWithStatusCode(OK, GSON.toJson(0), exchange);
        }
    }

    private <T> void responseTaskFoundWithSuccessStatus(T foundedTask, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(OK, 0);
        String toJson = GSON.toJson(foundedTask);
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
        exchange.sendResponseHeaders(NOT_FOUND, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(WRONG_METHOD.getBytes());
        }
        exchange.close();
    }

    private JsonObject getJsonObject(HttpExchange exchange) throws IOException {
        try (InputStreamReader isr = new InputStreamReader(exchange.getRequestBody());
             BufferedReader br = new BufferedReader(isr)) {
            int character;
            StringBuilder buffer = new StringBuilder(512);
            while ((character = br.read()) != -1) {
                buffer.append((char) character);
            }
            JsonElement jsonElement = JsonParser.parseString(buffer.toString());
            return jsonElement.getAsJsonObject();
        }
    }
}