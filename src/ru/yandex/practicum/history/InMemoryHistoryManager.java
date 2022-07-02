package ru.yandex.practicum.history;

import ru.yandex.practicum.task.Task;
import java.util.*;
/**
 * @author Vlad Osipov
 * @create 2022-05-09   19:08
 */
public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> browsingHistoryTasks = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (browsingHistoryTasks.containsKey(task.getId())) {
            Node deletedNode = browsingHistoryTasks.get(task.getId());
            removeNode(deletedNode);
            browsingHistoryTasks.remove(task.getId());
        }
        browsingHistoryTasks.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (browsingHistoryTasks.containsKey(id)) {
            Node deletedNode = browsingHistoryTasks.get(id);
            removeNode(deletedNode);
            browsingHistoryTasks.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private Node linkLast(Task task) {
        Node oldTail;
        if(browsingHistoryTasks.size() == 0) {
            oldTail = null;
        } else {
            oldTail = tail;
        }
        Node newTail = new Node(oldTail, task, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.setNext(newTail);
        }
        return newTail;
    }

    private void removeNode(Node node) {
        if (head == node) {
            head = head.getNext();
            if(head != null) {
                head.setPrev(null);
            }
        } else if (tail == node) {
            tail = tail.getPrev();
            tail.setNext(null);
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        if (head != null) {
            Node currentNode = head;
            while (currentNode.getNext() != null) {
                tasks.add(currentNode.getData());
                currentNode = currentNode.getNext();
            }
            tasks.add(currentNode.getData());
        }
        return tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryHistoryManager that = (InMemoryHistoryManager) o;
        return getHistory().equals(that.getHistory());
    }
}