import manager.FileBackedTaskManager;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager fileBackedTaskManager;

    @BeforeEach
    void beforeEach() throws IOException {
        tempFile = File.createTempFile("task_manager_test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void afterEach() {
        tempFile.delete();
    }

    @Test
    void shouldHandleEmptyFileCorrectly() {
        List<Task> tasks = fileBackedTaskManager.getAllTasks();
        Assertions.assertTrue(tasks.isEmpty(), "Список задач должен быть пустым после загрузки из пустого файла.");
    }

    @Test
    void shouldSaveAndLoadMultipleTasksCorrectly() {
        Task task1 = new Task("Task 1", "Description 1");
        fileBackedTaskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2");
        fileBackedTaskManager.createTask(task2);

        fileBackedTaskManager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> loadedTasks = loadedManager.getAllTasks();

        Assertions.assertEquals(2, loadedTasks.size(), "Должно быть загружено две задачи.");
        Assertions.assertEquals(task1, loadedTasks.get(0), "Первая задача должна совпадать с сохраненной.");
        Assertions.assertEquals(task2, loadedTasks.get(1), "Вторая задача должна совпадать с сохраненной.");
    }
}