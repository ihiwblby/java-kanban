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

    // Проверяем, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    public void shouldStorePreviousTaskInfo() {
        Task task = new Task("name", "description");
        taskManager.createTask(task);
        historyManager.add(task.clone());

        task.setName("updated name");
        taskManager.updateTask(task);
        historyManager.add(task.clone());

        List<Task> history = historyManager.getHistory();

        Assertions.assertNotNull(history, "История не пустая.");

        Assertions.assertEquals(2, history.size(), "В истории не сохранились обе задачи");

        Task originalTask = history.get(0);
        Assertions.assertEquals("name", originalTask.getName());

        Task updatedTask = history.get(1);
        Assertions.assertEquals("updated name", updatedTask.getName());
    }
}