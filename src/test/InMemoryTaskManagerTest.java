package test;

import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    // Проверяем, что эпик не добавляется в самого себя в виде подзадачи;
    @Test
    public void shouldNotAllowEpicToAddItselfAsSubtask() {
        Epic epic = new Epic("name", "description");
        taskManager.createEpic(epic);

        epic.addSubtaskId(epic.getId());

        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        Assertions.assertFalse(subtasksIds.contains(epic.getId()));
    }

    // Проверяем, что сабтаск не может быть своим же эпиком
    @Test
    public void shouldNotAllowSubtaskToHaveItselfAsEpic() {
        Epic epic = new Epic("name", "description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("name", "description", epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setMyEpicId(subtask.getId());

        Assertions.assertNotEquals(subtask.getId(), subtask.getMyEpicId(),
                "Сабтаск не должен быть своим эпиком");
    }

    // Проверяем, что задачи с заданным ID и сгенерированным ID не конфликтуют внутри менеджера
    @Test
    public void shouldNotConflictTasksWithGivenAndGeneratedIds() {
        Task givenIdTask = new Task("name", "description");
        givenIdTask.setId(10);
        taskManager.createTask(givenIdTask);

        Task generatedIdTask = new Task("name", "description");
        taskManager.createTask(generatedIdTask);

        Assertions.assertNotEquals(givenIdTask.getId(), generatedIdTask.getId());
        Assertions.assertEquals(2, taskManager.getAllTasks().size());
    }

    // Проверяем, что задачи разного типа действительно добавляются в TaskManager и могут быть найдены по ID
    @Test
    public void shouldAddAndFindTasksEpicsAndSubtasksById() {
        Task task = new Task("name", "description");
        taskManager.createTask(task);
        Epic epic = new Epic("name", "description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("name", "description", epic.getId());
        taskManager.createSubtask(subtask);

        Task foundTask = taskManager.getTaskById(task.getId());
        Epic foundEpic = taskManager.getEpicById(epic.getId());
        Subtask foundSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertEquals(task, foundTask);
        Assertions.assertEquals(epic, foundEpic);
        Assertions.assertEquals(subtask, foundSubtask);
    }

    // Проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    public void shouldStayUnchangeableAfterAddingToTaskManager() {
        Task task = new Task("name", "description");
        taskManager.createTask(task);

        Task checkedTask = taskManager.getTaskById(task.getId());

        Assertions.assertEquals(task.getName(), checkedTask.getName());
        Assertions.assertEquals(task.getDescription(), checkedTask.getDescription());
        Assertions.assertEquals(task.getId(), checkedTask.getId());
        Assertions.assertEquals(task.getStatus(), checkedTask.getStatus());
    }
}