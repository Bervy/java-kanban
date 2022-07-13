package ru.yandex.practicum.exceptions;

/**
 * @author Vlad Osipov
 * @create 2022-07-12   23:42
 */
public class ResponseException extends RuntimeException {

    public ResponseException(String message) {
        super(message);
    }
}