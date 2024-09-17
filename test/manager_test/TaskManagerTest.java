package manager_test;

import exceptions.NotFoundException;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utility.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public abstract class TaskManagerTest<T extends TaskManager> {

    protected InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldNotAllowEpicToAddItselfAsSubtask() {
        Epic epic = new Epic("name", "description");
        taskManager.createEpic(epic);

        epic.addSubtaskId(epic.getId());

        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        Assertions.assertFalse(subtasksIds.contains(epic.getId()));
    }

    @Test
    public void shouldNotAllowSubtaskToHaveItselfAsEpic() {
        Epic epic = new Epic("name", "description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("name1", "description1", epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setMyEpicId(subtask.getId());

        Assertions.assertNotEquals(subtask.getId(), subtask.getMyEpicId(),
                "Сабтаск не должен быть своим эпиком");
    }

    @Test
    public void shouldNotConflictTasksWithGivenAndGeneratedIds() {
        Task givenIdTask = new Task("name", "description");
        givenIdTask.setId(10);
        taskManager.createTask(givenIdTask);

        Task generatedIdTask = new Task("name1", "description1");
        taskManager.createTask(generatedIdTask);

        Assertions.assertNotEquals(givenIdTask.getId(), generatedIdTask.getId());
        Assertions.assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    public void shouldAddAndFindTasksEpicsAndSubtasksById() {
        Task task = new Task("name", "description");
        taskManager.createTask(task);
        Epic epic = new Epic("name1", "description1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("name2", "description2", epic.getId());
        taskManager.createSubtask(subtask);

        Task foundTask = taskManager.getTaskById(task.getId());
        Epic foundEpic = taskManager.getEpicById(epic.getId());
        Subtask foundSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertEquals(task, foundTask);
        Assertions.assertEquals(epic, foundEpic);
        Assertions.assertEquals(subtask, foundSubtask);
    }

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

    @Test
    public void shouldRemoveSubtaskFromEpic() {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask description", epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.removeSubtaskById(subtask.getId());

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertTrue(updatedEpic.getSubtasksIds().isEmpty(), "Список подзадач эпика должен быть пустым.");
    }

    @Test
    public void shouldUpdateEpicStatusBasedOnSubtasks() {
        Epic epic = new Epic("Epic 1", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description", epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description", epic.getId(), Duration.ofMinutes(10), LocalDateTime.now().plusMinutes(20));
        taskManager.createSubtask(subtask2);

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus(), "Статус эпика должен быть 'IN_PROGRESS'.");

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        updatedEpic = taskManager.getEpicById(epic.getId());
        Assertions.assertEquals(Status.DONE, updatedEpic.getStatus(), "Статус эпика должен быть 'DONE'.");
    }

    @Test
    public void shouldReflectChangesInTaskAfterSettingNewValues() {
        Task task = new Task("Task 1", "Task description");
        taskManager.createTask(task);

        task.setName("Updated Task");
        task.setDescription("Updated description");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        Assertions.assertEquals("Updated Task", updatedTask.getName());
        Assertions.assertEquals("Updated description", updatedTask.getDescription());
    }

    @Test
    public void shouldRemoveTaskFromHistoryAfterDeletion() {
        Task task = new Task("Task 1", "Task description");
        taskManager.createTask(task);
        int taskId = task.getId();

        taskManager.getTaskById(taskId);
        taskManager.removeTaskById(taskId);

        List<Task> history = taskManager.getHistory();
        Assertions.assertFalse(history.contains(task), "Задача не должна оставаться в истории после удаления.");
    }

    @Test
    public void shouldNotRetainSubtaskIdAfterDeletion() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", epic.getId());
        taskManager.createSubtask(subtask);

        Assertions.assertNotNull(taskManager.getSubtaskById(subtask.getId()), "Подзадача должна существовать после создания.");

        taskManager.removeSubtaskById(subtask.getId());

        Assertions.assertThrows(NotFoundException.class, () -> taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void shouldNotRetainNonExistentSubtaskIdsInEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", epic.getId());
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", epic.getId());
        taskManager.createSubtask(subtask2);

        Assertions.assertTrue(epic.getSubtasksIds().contains(subtask1.getId()), "ID первой подзадачи должен быть в списке ID эпика.");
        Assertions.assertTrue(epic.getSubtasksIds().contains(subtask2.getId()), "ID второй подзадачи должен быть в списке ID эпика.");

        taskManager.removeSubtaskById(subtask1.getId());

        Assertions.assertFalse(epic.getSubtasksIds().contains(subtask1.getId()), "ID удаленной подзадачи не должен присутствовать в списке подзадач эпика.");
        Assertions.assertTrue(epic.getSubtasksIds().contains(subtask2.getId()), "ID второй подзадачи все еще должен быть в списке подзадач эпика.");
    }

    @Test
    public void subtaskShouldHaveEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", epic.getId());
        taskManager.createSubtask(subtask1);

        Epic checkedEpic = taskManager.getEpicById(subtask1.getMyEpicId());
        Assertions.assertEquals("Epic 1", checkedEpic.getName());
        Assertions.assertEquals("Epic Description", checkedEpic.getDescription());
    }
}