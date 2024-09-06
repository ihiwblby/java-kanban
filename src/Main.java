import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
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

        Task task3 = new Task("Task 3", "Description 3");
        taskManager.createTask(task3);

        Epic epic1 = new Epic("Epic 1", "Epic description 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", epic1.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(15));
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", epic1.getId(), Duration.ofMinutes(20), LocalDateTime.now().plusMinutes(30));
        taskManager.createSubtask(subtask2);

        Subtask subtask3 = new Subtask("Subtask 3", "Subtask description 3", epic1.getId());
        taskManager.createSubtask(subtask3);

        System.out.println("Tasks: " + taskManager.getAllTasks());
        System.out.println("Epics: " + taskManager.getAllEpics());
        System.out.println("Subtasks: " + taskManager.getAllSubtasks());

        System.out.println("Epic start time: " + epic1.getStartTime());
        System.out.println("Epic end time: " + epic1.getEndTime());
        System.out.println("Epic duration: " + epic1.getDuration());
    }
}