package ru.yandex.practicum.exceptions;

/**
 * @author Vlad Osipov
 * @create 2022-06-14   23:52
 */
public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message) {
        super(message);
    }
}
