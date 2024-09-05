import manager.Managers;
import manager.TaskManager;
import model.Task;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(new File("./src/database/file_backed_task_manager.csv"));

        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(5));
        taskManager.createTask(task2);
        System.out.println(taskManager.getAllTasks());
    }
}