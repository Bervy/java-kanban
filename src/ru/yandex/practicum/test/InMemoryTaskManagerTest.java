package ru.yandex.practicum.test;

import ru.yandex.practicum.service.InMemoryTaskManager;
import ru.yandex.practicum.service.Managers;

/**
 * @author Vlad Osipov
 * @create 2022-06-28   8:22
 */
class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    InMemoryTaskManager createTaskManager() {
        return (InMemoryTaskManager) Managers.getInMemory();
    }
}