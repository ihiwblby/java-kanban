package test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    private Task createAndAddTask(String name, String description) {
        Task task = new Task(name, description);
        taskManager.createTask(task);
        historyManager.add(task.clone());
        return task;
    }

    private void assertHistorySize(int expectedSize, String message) {
        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(expectedSize, history.size(), message);
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task = createAndAddTask("name", "description");
        assertHistorySize(1, "История должна содержать 1 задачу.");
        Assertions.assertEquals(task, historyManager.getHistory().getFirst(), "Добавленная задача должна быть в истории.");
    }

    @Test
    public void shouldRemoveTaskFromHistoryById() {
        Task task1 = createAndAddTask("task1", "description1");
        Task task2 = createAndAddTask("task2", "description2");

        historyManager.remove(task1.getId());
        assertHistorySize(1, "История должна содержать 1 задачу после удаления.");
        Assertions.assertEquals(task2, historyManager.getHistory().getFirst(), "Оставшаяся задача должна быть task2.");
        Assertions.assertFalse(historyManager.getHistory().contains(task1), "История не должна содержать task1.");
    }

    @Test
    public void shouldHandleRemovingNonExistentTask() {
        Task task1 = createAndAddTask("task1", "description1");

        historyManager.remove(999);
        assertHistorySize(1, "История должна оставаться неизменной после попытки удаления несуществующей задачи.");
    }

    @Test
    public void shouldRemoveFirstTaskFromHistory() {
        Task task1 = createAndAddTask("Task 1", "Description 1");
        Task task2 = createAndAddTask("Task 2", "Description 2");
        Task task3 = createAndAddTask("Task 3", "Description 3");

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления.");
        Assertions.assertEquals(task2, history.getFirst(), "Первой задачей после удаления должна быть Task 2.");
    }

    @Test
    public void shouldRemoveLastTaskFromHistory() {
        Task task1 = createAndAddTask("Task 1", "Description 1");
        Task task2 = createAndAddTask("Task 2", "Description 2");
        Task task3 = createAndAddTask("Task 3", "Description 3");

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size(), "История должна содержать 2 задачи после удаления.");
        Assertions.assertEquals(task2, history.get(1), "Последней задачей после удаления должна быть Task 2.");
    }

    @Test
    public void shouldClearHistoryAfterRemovingOnlyTask() {
        Task task1 = createAndAddTask("Task 1", "Description 1");

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertTrue(history.isEmpty(), "История должна быть пустой после удаления единственной задачи.");
    }

    @Test
    public void shouldMaintainHistoryOrder() {
        Task task1 = createAndAddTask("Task 1", "Description 1");
        Task task2 = createAndAddTask("Task 2", "Description 2");
        Task task3 = createAndAddTask("Task 3", "Description 3");

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(task1, history.get(0), "Первой должна быть task1.");
        Assertions.assertEquals(task2, history.get(1), "Второй должна быть task2.");
        Assertions.assertEquals(task3, history.get(2), "Третьей должна быть task3.");
    }

    @Test
    public void shouldKeepOnlyOneViewForRepeatedTask() {
        Task task1 = createAndAddTask("Task 1", "Description for Task 1");
        Task task2 = createAndAddTask("Task 2", "Description for Task 2");
        Task task3 = createAndAddTask("Task 3", "Description for Task 3");

        historyManager.add(task1.clone());
        historyManager.add(task2.clone());
        historyManager.add(task3.clone());

        assertHistorySize(3, "История должна содержать 3 просмотра после добавления трех задач.");

        historyManager.add(task1.clone());
        historyManager.add(task1.clone());

        assertHistorySize(3, "История должна содержать 3 просмотра после многократного обращения к одной задаче.");

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(task1, history.get(2), "Последней задачей в истории должна быть задача 1.");
        Assertions.assertTrue(history.contains(task2), "Задача 2 должна быть в истории.");
        Assertions.assertTrue(history.contains(task3), "Задача 3 должна быть в истории.");
    }

    @Test
    public void shouldKeepOnlyLastViewOfUpdatedTask() {
        Task task = createAndAddTask("initial name", "initial description");

        historyManager.add(task.clone());
        assertHistorySize(1, "История должна содержать 1 просмотр после первого добавления.");

        task.setName("updated name");

        historyManager.add(task.clone());
        assertHistorySize(1, "История должна содержать 1 просмотр после обновления задачи.");

        Task lastViewedTask = historyManager.getHistory().getFirst();
        Assertions.assertEquals("updated name", lastViewedTask.getName(), "Задача в истории должна иметь обновленное имя.");
    }
}