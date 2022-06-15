package ru.yandex.practicum.history;

import ru.yandex.practicum.task.Task;

/**
 * @author Vlad Osipov
 * @create 2022-05-30   21:05
 */
public class Node {
    private final Task data;
    private Node next;
    private Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public Task getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Node{" + "data=" + data + ", next=" + next + ", prev=" + prev + '}';
    }
}
