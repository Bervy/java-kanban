package ru.yandex.practicum.server;

/**
 * @author Vlad Osipov
 * @create 2022-07-15   11:40
 */
public enum TaskResponseState {
    CREATED,
    UPDATED,
    ALREADY_EXISTS,
    OVERLAP_BY_TIME,
    DELETED,
    HAS_NULL_FIELDS,
    NOT_DELETED,
    NOT_FOUND
}
