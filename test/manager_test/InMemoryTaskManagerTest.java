package manager_test;

import manager.InMemoryTaskManager;

import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;


class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{
    @Test
    public void shouldNotAddOverlappingTasks() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(5));
        taskManager.createTask(task2);

        Assertions.assertEquals(1, taskManager.getAllTasks().size());
    }
}