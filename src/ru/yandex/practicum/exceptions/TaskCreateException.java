package ru.yandex.practicum.exceptions;

/**
 * @author Vlad Osipov
 * @create 2022-07-12   15:52
 */
public class TaskCreateException extends RuntimeException {

    public TaskCreateException(String message) {
        super(message);
    }
}