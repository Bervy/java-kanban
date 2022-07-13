package ru.yandex.practicum.exceptions;

/**
 * @author Vlad Osipov
 * @create 2022-07-02   23:30
 */
public class TaskOverlapAnotherTaskException extends RuntimeException {

    public TaskOverlapAnotherTaskException(String message) {
        super(message);
    }
}