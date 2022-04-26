package ru.yandex.practicum;

import ru.yandex.practicum.controller.Manager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.SubTask;
import ru.yandex.practicum.tasks.Task;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Task1", "Task1");
        Task task2 = new Task("Task2", "Task2");

        Epic epic1 = new Epic("epic1", "epic1");
        Epic epic2 = new Epic("epic2", "epic2");

        SubTask subTask1 = new SubTask(epic1, "subTask1", "subTask1");
        SubTask subTask2 = new SubTask(epic1, "subTask2", "subTask2");
        SubTask subTask3 = new SubTask(epic2, "subTask3", "subTask3");

        Manager manager = new Manager();

        manager.addTask(task1);
        manager.addTask(task2);

        manager.addEpic(epic1);
        manager.addEpic(epic2);

        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
        System.out.println("============================Тестирование задач========================\n");
        System.out.println("1. Получение списка всех задач.\n");

        for(Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }

//        System.out.println("===================================");
//        System.out.println("2. Удаление всех задач.\n ");
//        System.out.println("Список до удаления.\n");
//        for(Task task : manager.getListOfTasks()) {
//            System.out.println(task);
//        }
//        manager.removeAllTasks();
//
//        System.out.println("Список после удаления. ============================================\n");
//        if(manager.getListOfTasks().isEmpty()) {
//            System.out.println("Список задач пуст\n");
//        } else {
//            for(Task task : manager.getListOfTasks()) {
//                System.out.println(task);
//            }
//        }

        System.out.println("===================================");
        System.out.println("3. Получение задачи по идентификатору.\n ");
        System.out.println("Получение задачи по id=1.\n");
        System.out.println(manager.getTaskById(1));
        System.out.println("Получение задачи по id=0.\n");
        System.out.println(manager.getTaskById(0));
        System.out.println("===================================");
        System.out.println("4. Создание новой задачи.\n ");
        System.out.println("Список до создания новой задачи");

        for(Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }

        Task task3 = new Task("Task3", "Task3");
        System.out.println("Создалась новая задача " + manager.addTask(task3));
        System.out.println("Список после создания задачи");

        for(Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }

        System.out.println("===================================");
        System.out.println("5. Обновление задачи.\n ");
        System.out.println("Список до изменения состояния 1 задачи");

        for(Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }
        task1.changeState();
        System.out.println("1 задача изменила состояние на IN_PROGRESS" + manager.updateTask(task1));
        System.out.println("Список после изменения состояния 1 задачи");

        for(Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }

        System.out.println("===================================");
        System.out.println("6. Удаление задачи по идентификатору.\n ");
        System.out.println("Список до удаления 2 задачи");

        for(Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }
        System.out.println("Удаление задачи по id=1.\n");
        System.out.println("Удаление 2 задачи : " + manager.removeTaskById(1));
        System.out.println("Список после удаления 2 задачи");

        for(Task task : manager.getListOfTasks()) {
            System.out.println(task);
        }

        System.out.println("Получение задачи по id=0.\n");
        System.out.println(manager.getTaskById(0));

        System.out.println("============================Тестирование эпиков========================\n");
        System.out.println("1. Получение списка всех эпиков.\n");

        for(Epic epic : manager.getListOfEpics()) {
            System.out.println(epic);
        }

//        System.out.println("===================================");
//        System.out.println("2. Удаление всех эпиков.\n ");
//        System.out.println("Список до удаления.\n");
//        for(Task task : manager.getListOfEpics()) {
//            System.out.println(task);
//        }
//        manager.removeAllEpics();
//
//        System.out.println("Список после удаления. ============================================\n");
//        if(manager.getListOfEpics().isEmpty()) {
//            System.out.println("Список эпиков пуст\n");
//        } else {
//            for(Task task : manager.getListOfEpics()) {
//                System.out.println(task);
//            }
//        }

        System.out.println("===================================");
        System.out.println("3. Получение эпика по идентификатору.\n ");
        System.out.println("Получение эпика по id=1.\n");
        System.out.println(manager.getEpicById(1));
        System.out.println("Получение эпика по id=0.\n");
        System.out.println(manager.getEpicById(0));
        System.out.println("===================================");
        System.out.println("4. Создание нового эпика.\n ");
        System.out.println("Список до создания нового эпика");
        for(Task task : manager.getListOfEpics()) {
            System.out.println(task);
        }

        Epic epic3 = new Epic("epic3", "epic3");
        System.out.println("Создался новый эпик " + manager.addEpic(epic3));
        System.out.println("Список после создания нового эпика");

        for(Task task : manager.getListOfEpics()) {
            System.out.println(task);
        }

        System.out.println("===================================");
        System.out.println("5. Обновление эпиков.\n ");
        System.out.println("Список до изменения состояния 1 эпика");

        for(Epic epic : manager.getListOfEpics()) {
            System.out.println(epic);
        }
        epic1.setName("epic11");
        System.out.println("1 эпик изменил название на epic11" + manager.updateEpic(epic1));
        System.out.println("Список после изменения состояния 1 эпика");
        for(Epic epic : manager.getListOfEpics()) {
            System.out.println(epic);
        }

        System.out.println("===================================");
        System.out.println("6. Удаление эпика' по идентификатору.\n ");
        System.out.println("Список до удаления 1 эпика");

        for(Task task : manager.getListOfEpics()) {
            System.out.println(task);
        }
        System.out.println("Удаление эпика по id=1.\n");
        System.out.println("Удаление 1 эпика : " + manager.removeEpicById(1));
        System.out.println("Список после удаления 2 эпика");
        for(Task task : manager.getListOfEpics()) {
            System.out.println(task);
        }

        System.out.println("============================Тестирование подзадач========================\n");
        System.out.println("1. Получение списка всех подзадач.\n");

        for(Task task : manager.getListOfSubTasks()) {
            System.out.println(task);
        }

//        System.out.println("===================================");
//        System.out.println("2. Удаление всех подзадач.\n ");
//        System.out.println("Список до удаления.\n");
//        for(Task task : manager.getListOfSubTasks()) {
//            System.out.println(task);
//        }
//        manager.removeAllSubTasks();
//
//        System.out.println("Список после удаления. ============================================\n");
//        if(manager.getListOfSubTasks().isEmpty()) {
//            System.out.println("Список эпиков пуст\n");
//        } else {
//            for(Task task : manager.getListOfSubTasks()) {
//                System.out.println(task);
//            }
//        }

        System.out.println("===================================");
        System.out.println("3. Получение сабтаска по идентификатору.\n ");
        System.out.println("Получение сабтаска по id=3.\n");
        System.out.println(manager.getSubTaskById(3));
        System.out.println("Получение сабтаска по id=1.\n");
        System.out.println(manager.getSubTaskById(1));
        System.out.println("===================================");
        System.out.println("4. Создание нового сабтаска.\n ");
        System.out.println("Список до создания нового сабтаска");
        for(Task task : manager.getListOfSubTasks()) {
            System.out.println(task);
        }

        SubTask subTask4 = new SubTask(epic2, "subTask4", "subTask4");
        System.out.println("Создался новый сабтаск " + manager.addSubTask(subTask4));
        System.out.println("Список после создания нового сабтаска");

        for(Task task : manager.getListOfSubTasks()) {
            System.out.println(task);
        }

        System.out.println("===================================");
        System.out.println("5. Обновление сабтаска.\n ");
        System.out.println("Список до изменения состояния 3 сабтаска");
        for(Task task : manager.getListOfSubTasks()) {
            System.out.println(task);
        }
        subTask3.changeState();

        System.out.println("3 подзадача изменила состояние на IN_PROGRESS " + manager.updateSubTask(subTask3));
        System.out.println("Список подзадач после изменения состояния 3 подзадачи");
        for(Task task : manager.getListOfSubTasks()) {
            System.out.println(task);
        }

        System.out.println("Список эпиков после изменения состояния 3 подзадачи");
        for(Epic epic : manager.getListOfEpics()) {
            System.out.println(epic);
        }

        System.out.println("===================================");
        System.out.println("6. Удаление подзадачи по идентификатору.\n ");
        System.out.println("Список до удаления 3 подзадачи");
        for(Task task : manager.getListOfSubTasks()) {
            System.out.println(task);
        }

        System.out.println("Удаление задачи по id=1.\n");
        System.out.println("Удаление 3 подзадачи : " + manager.removeSubTaskById(3));
        System.out.println("Список после удаления 3 подзадачи");
        for(Task task : manager.getListOfSubTasks()) {
            System.out.println(task);
        }
        System.out.println(manager.getSubTasksOfEpic(epic1));
    }
}