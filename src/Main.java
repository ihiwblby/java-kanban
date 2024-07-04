import manager.Managers;
import manager.Status;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        // Создание задач, эпиков, сабтасков
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.createEpic(epic3);

        Subtask subtask1 = new Subtask("1 подзадача 1 эпика", "Описание подзадачи 1.1",
                epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("2 подзадача 1 эпика", "Описание подзадачи 1.2",
                epic1.getId());
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("1 подзадача 2 эпика", "Описание подзадачи 2.1",
                epic2.getId());
        taskManager.createSubtask(subtask3);

        // Проверка сохранения всех задач, эпиков и сабтасков. История должна быть пустой
        getHistory(taskManager);
        System.out.println("----------");
        System.out.println();

        // Вызов методов интерфейса TaskManager, использование которых отражается в истории
        taskManager.removeEpicById(epic1.getId());
        taskManager.removeSubtaskById(subtask3.getId());

        // Проверка истории. История должна обновиться
        getHistory(taskManager);
        System.out.println("----------");
        System.out.println();

        // Вызов методов интерфейса TaskManager, использование которых НЕ отражается в истории
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.removeTaskById(task2.getId());

        // Проверка истории. История не должна обновиться
        getHistory(taskManager);
        System.out.println("----------");
        System.out.println();
    }

    private static void getHistory(TaskManager manager) {
        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}