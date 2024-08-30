import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault(new File("./src/database/file_backed_task_manager.csv"));

        // 1. Создайте две задачи, эпик с тремя подзадачами и эпик без подзадач.
        Task task1 = new Task("Task 1", "Description of Task 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Task 2", "Description of Task 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic with subtasks");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Subtask 1", "Description of Subtask 1", epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description of Subtask 2", epic1.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Subtask 3", "Description of Subtask 3", epic1.getId());
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Epic 2", "Epic without subtasks");
        taskManager.createEpic(epic2);

        // 2. Запросите созданные задачи несколько раз в разном порядке.
        // 3. После каждого запроса выведите историю и убедитесь, что в ней нет повторов.
        System.out.println("Пункт 2");
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getSubtaskById(subtask3.getId());
        getHistory(taskManager);

        taskManager.getTaskById(task1.getId());
        getHistory(taskManager);

        taskManager.getEpicById(epic1.getId());
        taskManager.getEpicById(epic1.getId());
        getHistory(taskManager);

        // 4. Удалите задачу, которая есть в истории, и проверьте, что при печати она не будет выводиться.
        System.out.println("Пункт 4");
        taskManager.removeTaskById(task1.getId());
        getHistory(taskManager);

        // 5. Удалите эпик с тремя подзадачами и убедитесь, что из истории удалился как сам эпик, так и все его подзадачи.
        System.out.println("Пункт 5");
        taskManager.removeEpicById(epic1.getId());
        getHistory(taskManager);
    }

    private static void getHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
        System.out.println("----------");
        System.out.println();
    }
}