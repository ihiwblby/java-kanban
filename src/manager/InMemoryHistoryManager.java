package manager;

import model.Task;
import utility.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> history = new HashMap<>();

    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Невозможно добавить пустую задачу");
            return;
        }
        int taskId = task.getId();

        if (history.containsKey(taskId)) {
            remove(taskId);
        }
        history.put(taskId, linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        if (node == null) {
            System.out.println("По данному ID не сущетсвует задачи");
            return;
        }
        removeNode(node);
        history.remove(id);
    }

    private Node linkLast(Task data) {
        final Node oldTail = tail;
        final Node newTail = new Node(oldTail, data, null);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.setNext(newTail);
        }
        return tail;
    }

    private ArrayList<Task> getTasks() {
        final ArrayList<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.getData());
            node = node.getNext();
        }
        return tasks;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        Node prevNode = node.getPrev();
        Node nextNode = node.getNext();

        if (prevNode == null) {
            head = nextNode;
            if (nextNode != null) {
                nextNode.setPrev(null);
            }
        } else {
            prevNode.setNext(nextNode);
        }

        if (nextNode == null) {
            tail = prevNode;
            if (prevNode != null) {
                prevNode.setNext(null);
            }
        } else {
            nextNode.setPrev(prevNode);
        }
    }
}
