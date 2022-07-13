package ru.yandex.practicum.exceptions;

/**
 * @author Vlad Osipov
 * @create 2022-07-11   22:22
 */
public class ServerCreateException extends RuntimeException {

    public ServerCreateException(String message) {
        super(message);
    }
}