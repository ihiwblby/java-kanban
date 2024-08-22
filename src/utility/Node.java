package utility;

import model.Task;

public class Node {
    protected Task data;
    protected Node next;
    protected Node prev;

    public Node(Node prev, Task data, Node next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Task getData() {
        return data;
    }

    public void setData(Task data) {
        this.data = data;
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
}
