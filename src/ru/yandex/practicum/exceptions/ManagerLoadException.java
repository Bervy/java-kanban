package ru.yandex.practicum.exceptions;

/**
 * @author Vlad Osipov
 * @create 2022-06-15   0:15
 */
public class ManagerLoadException extends RuntimeException{

    public ManagerLoadException(String message) {
        super(message);
    }
}
