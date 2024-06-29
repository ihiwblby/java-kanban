public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Создание двух задач
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.createTask(task2);

        // Создание эпика с двумя подзадачами
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("1 подзадача 1 эпика", "Описание подзадачи 1.1",
                epic1.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("2 подзадача 1 эпика", "Описание подзадачи 1.2",
                epic1.getId());
        taskManager.createSubtask(subtask2);

        // Создание эпика с одной подзадачей
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2");
        taskManager.createEpic(epic2);
        Subtask subtask3 = new Subtask("1 подзадача 2 эпика", "Описание подзадачи 2.1",
                epic2.getId());
        taskManager.createSubtask(subtask3);
        Epic epic3 = new Epic("Эпик 3", "Описание эпика 3");
        taskManager.createEpic(epic3);

        /*Subtask s = taskManager.getSubtaskById(3);
        s.setStatus(Status.DONE);
        Subtask s2 = taskManager.getSubtaskById(4);
        s2.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(s);
        taskManager.updateSubtask(s2);
        System.out.println(taskManager.getEpicById(epic1.getId()).getStatus());
        taskManager.removeSubtaskById(s.id);

        System.out.println(taskManager.getEpicById(epic1.getId()).getStatus());*/

        // Вывод всех задач, эпиков и подзадач
        System.out.println("-----All Tasks-----");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("-----All Epics-----");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println();

        System.out.println("-----All Subtasks-----");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
        System.out.println();

        // Изменение статусов задач и подзадач
        task1.setStatus(Status.IN_PROGRESS);
        task2.setStatus(Status.DONE);
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.updateEpicStatus(epic1.getId());
        subtask3.setStatus(Status.NEW);
        taskManager.updateEpicStatus(epic2.getId());

        // Проверка измененных статусов
        System.out.println("-----Updated Statuses-----");
        System.out.println(task1);
        System.out.println(task2);
        System.out.println(epic1);
        System.out.println(epic2);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(subtask3);
        System.out.println();

        // Удаление задачи и эпика
        taskManager.removeTaskById(task1.getId());
        taskManager.removeEpicById(epic1.getId());

        // Вывод всех задач, эпиков и подзадач после удаления
        System.out.println("-----All Tasks after removing-----");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println();

        System.out.println("-----All Epics after removing-----");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic);
        }
        System.out.println();

        System.out.println("-----All Subtasks after removing-----");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask);
        }
    }
}