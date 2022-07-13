package ru.yandex.practicum.exceptions;

/**
 * @author Vlad Osipov
 * @create 2022-07-11   22:14
 */
public class TaskClientRegisterException extends RuntimeException {

    public TaskClientRegisterException(String message) {
        super(message);
    }
}